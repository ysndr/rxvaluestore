package de.ysndr.rxvaluestore;

import org.immutables.value.Value;

/**
 * Created by yannik on 2/22/17.
 */
@Value.Style(
        typeAbstract = "*Def",
        typeImmutable = "*",
        allParameters = true, // all attributes will become constructor parameters
        // as if they are annotated with @Value.Parameter
        visibility = Value.Style.ImplementationVisibility.PUBLIC, // Generated class will be always public
        defaults = @Value.Immutable
) // Disable copy methods and builder
public @interface RxValueStore {}
