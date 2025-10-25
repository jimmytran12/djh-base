package com.djh.base.mapper;

import com.djh.base.common.config.factory.ObjectFactory;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.stream.Collectors;

public interface MapperExam<S, T> {
    T sToT(S s, @MappingTarget T b);

    T sToT(S a);

    Collection<T> ssToTs(Collection<S> as);

    default Collection<T> asToTs(Collection<S> as, ObjectFactory<T> objectFactory) {
        return as.stream().map(a1 -> this.sToT(a1, objectFactory.create())).collect(Collectors.toList());

    }
}
