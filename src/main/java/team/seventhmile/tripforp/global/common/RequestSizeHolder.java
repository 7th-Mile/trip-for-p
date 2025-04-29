package team.seventhmile.tripforp.global.common;

public class RequestSizeHolder {

    private static final ThreadLocal<Integer> actualSize = new ThreadLocal<>();

    public static void setSize(Integer size) {
        actualSize.set(size);
    }

    public static Integer getSize() {
        return actualSize.get();
    }

    public static void clear() {
        actualSize.remove();
    }
}
