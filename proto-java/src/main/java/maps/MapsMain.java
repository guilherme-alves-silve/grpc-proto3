package maps;

import example.maps.Maps;

import java.util.Map;

public class MapsMain {

    public static void main(String[] args) {
        Maps.MapExample message = Maps.MapExample.newBuilder()
                .putIds("myid", newIdWrapper(42))
                .putIds("myid2", newIdWrapper(43))
                .putAllIds(Map.of(
                        "myid3", newIdWrapper(44),
                        "myid4", newIdWrapper(45),
                        "myid5", newIdWrapper(46)
                ))
                .build();

        System.out.println(message);
    }

    private static Maps.IdWrapper newIdWrapper(int id) {
        return Maps.IdWrapper.newBuilder()
                .setId(id)
                .build();
    }
}
