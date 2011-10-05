package org.nbfx.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class ObjectConverter<INPUT, OUTPUT> {

    private final Map<INPUT, OUTPUT> childNodeMap = new WeakHashMap<INPUT, OUTPUT>();

    public Collection<OUTPUT> getConverted(final List<? extends INPUT> inputs) {
        final List<OUTPUT> outputs = new ArrayList<OUTPUT>(inputs.size());

        synchronized (childNodeMap) {
            if ((null == inputs) || inputs.isEmpty()) {
                return outputs;
            }

            for (final INPUT input : inputs) {
                OUTPUT output = childNodeMap.get(input);

                if (null == output) {
                    output = convert(input);
                    childNodeMap.put(input, output);
                }

                outputs.add(output);
            }
        }

        return outputs;
    }

    protected abstract OUTPUT convert(final INPUT input);
}
