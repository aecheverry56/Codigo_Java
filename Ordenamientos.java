import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/** Implementaciones educativas originales. Arreglos y elementos no nulos. */
public final class Ordenamientos {
    private Ordenamientos() { }
    // BEGIN comun
    static final Comparator<Evento> POR_PRIORIDAD =
        Comparator.comparingInt(Evento::getPrioridad);
    static final Comparator<Evento> POR_FECHA =
        Comparator.comparing(Evento::getFechaCreacion);
    static final Comparator<Evento> ATENCION = POR_PRIORIDAD
        .thenComparing(Evento::getFechaCreacion)
        .thenComparingInt(Evento::getId);
    static void swap(Evento[] a, int i, int j) {
        Evento t = a[i]; a[i] = a[j]; a[j] = t;
    }
    // END comun
    // BEGIN bubble
    public static void bubble(Evento[] a, Comparator<Evento> c) {
        for (int fin = a.length - 1; fin > 0; fin--) {
            boolean cambio = false;
            for (int j = 0; j < fin; j++) {
                if (c.compare(a[j], a[j + 1]) > 0) {
                    swap(a, j, j + 1);
                    cambio = true;
                }
            }
            if (!cambio) return;
        }
    }
    // END bubble
    // BEGIN selection
    public static void selection(Evento[] a, Comparator<Evento> c) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++)
                if (c.compare(a[j], a[min]) < 0) min = j;
            if (min != i) swap(a, i, min);
        }
    }
    // END selection
    // BEGIN insertion
    public static void insertion(Evento[] a, Comparator<Evento> c) {
        for (int i = 1; i < a.length; i++) {
            Evento x = a[i];
            int j = i - 1;
            while (j >= 0 && c.compare(a[j], x) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = x;
        }
    }
    // END insertion
    // BEGIN merge
    public static void merge(Evento[] a, Comparator<Evento> c) {
        mergeR(a, new Evento[a.length], 0, a.length, c);
    }
    private static void mergeR(Evento[] a, Evento[] t,
                              int l, int r, Comparator<Evento> c) {
        if (r - l < 2) return;
        int m = l + (r - l) / 2;
        mergeR(a, t, l, m, c);
        mergeR(a, t, m, r, c);
        int i = l, j = m;
        for (int k = l; k < r; k++) {
            if (i < m && (j == r || c.compare(a[i], a[j]) <= 0))
                t[k] = a[i++];
            else t[k] = a[j++];
        }
        System.arraycopy(t, l, a, l, r - l);
    }
    // END merge
    // BEGIN quick
    public static void quick(Evento[] a, Comparator<Evento> c) {
        quickR(a, 0, a.length - 1, c);
    }
    private static void quickR(Evento[] a, int l, int r,
                               Comparator<Evento> c) {
        if (l >= r) return;
        int p = ThreadLocalRandom.current().nextInt(l, r + 1);
        swap(a, p, r);
        Evento pivote = a[r];
        int i = l;
        for (int j = l; j < r; j++)
            if (c.compare(a[j], pivote) < 0) swap(a, i++, j);
        swap(a, i, r);
        quickR(a, l, i - 1, c);
        quickR(a, i + 1, r, c);
    }
    // END quick
    // BEGIN heap
    public static void heap(Evento[] a, Comparator<Evento> c) {
        for (int i = a.length / 2 - 1; i >= 0; i--)
            bajar(a, a.length, i, c);
        for (int fin = a.length - 1; fin > 0; fin--) {
            swap(a, 0, fin);
            bajar(a, fin, 0, c);
        }
    }
    private static void bajar(Evento[] a, int n, int i,
                              Comparator<Evento> c) {
        while (i < n / 2) {
            int h = 2 * i + 1;
            if (h + 1 < n && c.compare(a[h], a[h + 1]) < 0) h++;
            if (c.compare(a[i], a[h]) >= 0) break;
            swap(a, i, h);
            i = h;
        }
    }
    // END heap
    // BEGIN counting
    public static void counting(Evento[] a) {
        int[] cuenta = new int[10];
        Evento[] salida = new Evento[a.length];
        for (Evento e : a) cuenta[e.getPrioridad()]++;
        for (int p = 1; p < 10; p++) cuenta[p] += cuenta[p - 1];
        for (int i = a.length - 1; i >= 0; i--) {
            Evento e = a[i];
            salida[--cuenta[e.getPrioridad()]] = e;
        }
        System.arraycopy(salida, 0, a, 0, a.length);
    }
    // END counting
    // BEGIN radix
    public static void radix(Evento[] a) {
        int max = 0;
        for (Evento e : a) max = Math.max(max, e.getId());
        for (long exp = 1; max / exp > 0; exp *= 10)
            digito(a, exp);
    }
    private static void digito(Evento[] a, long exp) {
        int[] cuenta = new int[10];
        Evento[] salida = new Evento[a.length];
        for (Evento e : a) cuenta[(int)(e.getId() / exp % 10)]++;
        for (int d = 1; d < 10; d++) cuenta[d] += cuenta[d - 1];
        for (int i = a.length - 1; i >= 0; i--) {
            int d = (int)(a[i].getId() / exp % 10);
            salida[--cuenta[d]] = a[i];
        }
        System.arraycopy(salida, 0, a, 0, a.length);
    }
    // END radix
    // BEGIN bucket
    public static void bucket(Evento[] a) {
        List<List<Evento>> cubetas = new ArrayList<>();
        for (int i = 0; i < 5; i++) cubetas.add(new ArrayList<>());
        for (Evento e : a)
            cubetas.get(e.getPrioridad() / 2).add(e);
        int pos = 0;
        for (List<Evento> cubeta : cubetas) {
            Evento[] tramo = cubeta.toArray(new Evento[0]);
            insertion(tramo, POR_PRIORIDAD);
            for (Evento e : tramo) a[pos++] = e;
        }
    }
    // END bucket
    // BEGIN shell
    public static void shell(Evento[] a, Comparator<Evento> c) {
        for (int h = a.length / 2; h > 0; h /= 2) {
            for (int i = h; i < a.length; i++) {
                Evento x = a[i];
                int j = i;
                while (j >= h && c.compare(a[j - h], x) > 0) {
                    a[j] = a[j - h];
                    j -= h;
                }
                a[j] = x;
            }
        }
    }
    // END shell
}
