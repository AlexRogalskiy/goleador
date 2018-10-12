package ris58h.goleador.core.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pipeline {
    private final List<ProcessorDescriptor> processorDescriptors;

    private Pipeline(List<ProcessorDescriptor> processorDescriptors) {
        this.processorDescriptors = processorDescriptors;
    }

    public static Pipeline create(String pipelineDescription, ProcessorFactory processorFactory) {
        String[] split = pipelineDescription.split(",");
        if (split.length == 0) {
            throw new RuntimeException("No processors specified!");
        }
        List<ProcessorDescriptor> processorDescriptors = new ArrayList<>();
        for (String description : split) {
            boolean disabled = description.startsWith("-");
            String nameAndType = disabled ? description.substring(1) : description;
            int colonIndex = nameAndType.indexOf(':');
            String name = colonIndex >= 0 ? nameAndType.substring(0, colonIndex) : nameAndType;
            String type = colonIndex >= 0 ? nameAndType.substring(colonIndex + 1, nameAndType.length()) : nameAndType;
            Processor processor = null;
            if (!disabled) {
                processor = processorFactory.create(type);
                if (processor == null) {
                    throw new RuntimeException("Couldn't create processor with type " + type);
                }
            }
            processorDescriptors.add(new ProcessorDescriptor(processor, name, disabled));
        }
        return new Pipeline(processorDescriptors);
    }

    public void init(Parameters parameters) throws Exception {
        for (ProcessorDescriptor processorDescriptor : processorDescriptors) {
            if (!processorDescriptor.disabled) {
                Parameters processorParameters = Parameters.subParameters(parameters, processorDescriptor.name);
                processorDescriptor.processor.init(processorParameters);
            }
        }
    }

    public void process(String in, String out, String workingDir) throws Exception {
        String curIn = in;
        Iterator<ProcessorDescriptor> iterator = processorDescriptors.iterator();
        boolean hasNext;
        do {
            ProcessorDescriptor processorDescriptor = iterator.next();
            String processorName = processorDescriptor.name;
            Processor processor = processorDescriptor.processor;
            hasNext = iterator.hasNext();
            String curOut = hasNext || out == null ? processorName : out;
            if (!processorDescriptor.disabled) {
                processor.process(curIn, curOut, workingDir);
            }
            curIn = curOut;
        } while (hasNext);
    }

    public void dispose() throws Exception {
        for (ProcessorDescriptor processorDescriptor : processorDescriptors) {
            if (!processorDescriptor.disabled) {
                processorDescriptor.processor.dispose();
            }
        }
    }

    private static class ProcessorDescriptor {
        final Processor processor;
        final String name;
        final boolean disabled;

        ProcessorDescriptor(Processor processor, String name, boolean disabled) {
            this.processor = processor;
            this.name = name;
            this.disabled = disabled;
        }
    }
}
