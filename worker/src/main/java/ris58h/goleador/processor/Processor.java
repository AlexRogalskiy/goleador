package ris58h.goleador.processor;

public interface Processor {
    default void init(Props properties) throws Exception {}

    void process(String inName, String outName, String workingDir) throws Exception;

    default void dispose() throws Exception {}
}
