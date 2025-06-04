package jvlang.model;

/**
 * 返回值异常
 * @author Yumerain
 */
public class ReturnSignal extends RuntimeException {

    public final Object value;

    public ReturnSignal(Object value) {
        this.value = value;
    }

}
