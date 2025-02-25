package com.schoolmanagement.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassRoomIdGenerator implements IdentifierGenerator {
    private String prefix;
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        prefix = params.getProperty("prefix");
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return prefix + counter.incrementAndGet();
    }
}