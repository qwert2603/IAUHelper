package com.qwert2603.iau_test;

import com.qwert2603.iau_helper.Callback;
import com.qwert2603.iau_helper.Logger;
import com.qwert2603.iau_helper.UpdateHelperFlexible;
import org.jetbrains.annotations.NotNull;

public class FromJava {
    void d() {
        new UpdateHelperFlexible(
                new FlexibleActivity(),
                new Callback() {
                    @Override
                    public void invoke() {

                    }
                },
                new Logger() {
                    @Override
                    public void log(@NotNull String s) {

                    }
                }
        );
    }
}
