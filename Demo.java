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
    public static void main(String[] args) {
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
    }
}
