import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class Demo {
    static Evento e(int id, int p) {
        return new Evento(id, Instant.parse("2026-01-01T00:00:00Z"),
                          "Evento " + id, p);
    }
    static void verificar(String nombre, Consumer<Evento[]> ordenar,
                          Comparator<Evento> criterio, boolean estable) {
        Random rnd = new Random(2024);
        for (int caso = 0; caso < 250; caso++) {
            int n = caso < 5 ? caso : rnd.nextInt(150);
            Evento[] a = new Evento[n];
            for (int i = 0; i < n; i++) a[i] = e(rnd.nextInt(500), rnd.nextInt(10));
            if (caso % 3 == 0) Arrays.sort(a, criterio);
            if (caso % 3 == 1) Arrays.sort(a, criterio.reversed());
            Evento[] esperado = a.clone();
            Arrays.sort(esperado, criterio);
            ordenar.accept(a);
            for (int i = 0; i < n; i++) {
                if (criterio.compare(a[i], esperado[i]) != 0)
                    throw new AssertionError(nombre + ": orden incorrecto");
                if (estable && a[i] != esperado[i])
                    throw new AssertionError(nombre + ": estabilidad");
            }
            Set<Evento> originales = Collections.newSetFromMap(new IdentityHashMap<>());
            originales.addAll(Arrays.asList(esperado));
            for (Evento item : a)
                if (!originales.remove(item)) throw new AssertionError("Permutacion");
            if (!originales.isEmpty()) throw new AssertionError("Perdida");
        }
        System.out.println(nombre + ": 250 casos OK");
    }
    static void mostrarFechas(String titulo, Evento[] eventos) {
        System.out.println("\n" + titulo);
        for (Evento evento : eventos) {
            System.out.println(evento.getFechaCreacion() + " | " + evento.getDescripcion());
        }
    }

    static void ejemplosPorFecha() {
        Evento[] eventos = {
            new Evento(1, Instant.parse("2026-11-12T15:00:00Z"), "Entrega del taller", 1),
            new Evento(2, Instant.parse("2026-11-10T15:00:00Z"), "Inicio del taller", 3),
            new Evento(3, Instant.parse("2026-11-12T14:00:00Z"), "Revision del taller", 2)
        };
        Comparator<Evento> porFecha = Ordenamientos.POR_FECHA;
        // Cada algoritmo recibe una copia del mismo arreglo desordenado.
        Evento[] burbuja = eventos.clone();
        Evento[] seleccion = eventos.clone();
        Evento[] insercion = eventos.clone();

        mostrarFechas("Antes de ordenar", eventos);
        Ordenamientos.bubble(burbuja, porFecha);
        mostrarFechas("1. Burbuja por fecha", burbuja);
        Ordenamientos.selection(seleccion, porFecha);
        mostrarFechas("2. Seleccion por fecha", seleccion);
        Ordenamientos.insertion(insercion, porFecha);
        mostrarFechas("3. Insercion por fecha", insercion);
    }

    public static void probarTiempos(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Random aleatorio = new Random(2026);
        Instant inicio = Instant.parse("2026-01-01T00:00:00Z");
        Evento[] eventos = new Evento[cantidad];
        for (int i = 0; i < cantidad; i++) {
            int id = aleatorio.nextInt(Integer.MAX_VALUE);
            Instant fecha = inicio.plusSeconds(aleatorio.nextInt(365 * 24 * 60 * 60));
            int prioridad = aleatorio.nextInt(10);
            eventos[i] = new Evento(id, fecha, "Evento " + (i + 1), prioridad);
        }

        Comparator<Evento> fecha = Ordenamientos.POR_FECHA;
        System.out.printf("%nTiempos para %,d eventos (mediana de 3 pruebas)%n", cantidad);
        medirTiempo("Burbuja", "fecha", eventos, a -> Ordenamientos.bubble(a, fecha));
        medirTiempo("Seleccion", "fecha", eventos, a -> Ordenamientos.selection(a, fecha));
        medirTiempo("Insercion", "fecha", eventos, a -> Ordenamientos.insertion(a, fecha));
        medirTiempo("Merge", "fecha", eventos, a -> Ordenamientos.merge(a, fecha));
        medirTiempo("Quick", "fecha", eventos, a -> Ordenamientos.quick(a, fecha));
        medirTiempo("Heap", "fecha", eventos, a -> Ordenamientos.heap(a, fecha));
        medirTiempo("Counting", "prioridad", eventos, Ordenamientos::counting);
        medirTiempo("Radix", "ID", eventos, Ordenamientos::radix);
        medirTiempo("Bucket", "prioridad", eventos, Ordenamientos::bucket);
        medirTiempo("Shell", "fecha", eventos, a -> Ordenamientos.shell(a, fecha));
    }

    private static void medirTiempo(String nombre, String criterio, Evento[] eventos,
                                    Consumer<Evento[]> ordenar) {
        ordenar.accept(eventos.clone()); // Calentamiento antes de medir.
        long[] tiempos = new long[3];
        for (int i = 0; i < tiempos.length; i++) {
            Evento[] copia = eventos.clone();
            long inicio = System.nanoTime();
            ordenar.accept(copia);
            tiempos[i] = System.nanoTime() - inicio;
        }
        Arrays.sort(tiempos);
        System.out.printf("%-10s %-10s %10.3f ms%n", nombre, criterio, tiempos[1] / 1_000_000.0);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            probarTiempos(Integer.parseInt(args[0]));
            return;
        }
        Comparator<Evento> c = Ordenamientos.POR_PRIORIDAD;
        verificar("Bubble", a -> Ordenamientos.bubble(a,c), c, true);
        verificar("Selection", a -> Ordenamientos.selection(a,c), c, false);
        verificar("Insertion", a -> Ordenamientos.insertion(a,c), c, true);
        verificar("Merge", a -> Ordenamientos.merge(a,c), c, true);
        verificar("Quick", a -> Ordenamientos.quick(a,c), c, false);
        verificar("Heap", a -> Ordenamientos.heap(a,c), c, false);
        verificar("Counting", Ordenamientos::counting, c, true);
        verificar("Radix", Ordenamientos::radix,
                  Comparator.comparingInt(Evento::getId), true);
        verificar("Bucket", Ordenamientos::bucket, c, true);
        verificar("Shell", a -> Ordenamientos.shell(a,c), c, false);
        Evento[] extremos = {e(Integer.MAX_VALUE,9), e(0,0), e(10,1)};
        Ordenamientos.radix(extremos);
        if (extremos[2].getId() != Integer.MAX_VALUE) throw new AssertionError();
        Evento[] todosIguales = new Evento[200];
        for (int i=0;i<200;i++) todosIguales[i]=e(i,3);
        Ordenamientos.quick(todosIguales,c);
        Evento[] ejemplo = {e(170,3),e(45,1),e(75,3),e(90,0),e(802,1),e(24,2)};
        Ordenamientos.counting(ejemplo);
        System.out.println(Arrays.toString(ejemplo));
        ejemplosPorFecha();
        probarTiempos(10_000);
    }
}
