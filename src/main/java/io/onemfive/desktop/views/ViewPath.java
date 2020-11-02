package io.onemfive.desktop.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ViewPath extends ArrayList<Class<? extends View>> {

    private ViewPath() {}

    public ViewPath(Collection<? extends Class<? extends View>> c) {
        super(c);
    }

    @SafeVarargs
    public static ViewPath to(Class<? extends View>... elements) {
        ViewPath path = new ViewPath();
        List<Class<? extends View>> list = Arrays.asList(elements);
        path.addAll(list);
        return path;
    }

    public static ViewPath from(ViewPath original) {
        ViewPath path = new ViewPath();
        path.addAll(original);
        return path;
    }

    public Class<? extends View> tip() {
        if (size() == 0)
            return null;
        return get(size() - 1);
    }
}
