package com.princess.android.rxjavaexample.di.components;

import com.princess.android.rxjavaexample.NoteApplication;
import com.princess.android.rxjavaexample.di.modules.BuildersModule;
import com.princess.android.rxjavaexample.di.modules.NoteApplicationModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
                    NoteApplicationModule.class,
                    BuildersModule.class})
public interface NoteApplicationComponent {

    @Component.Builder
    interface Builder{
        @BindsInstance
        Builder application(NoteApplication noteApplication);
        NoteApplicationComponent build();
    }
    void inject(NoteApplication noteApplication);
}
