package com.fasterxml.jackson.module.jaxb;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Module that can be registered to add support for JAXB annotations.
 * It does basically equivalent of
 *<pre>
 *   objectMapper.setAnnotationIntrospector(...);
 *</pre>
 * with combination of {@link JaxbAnnotationIntrospector} and existing
 * default introspector(s) (if any), depending on configuration
 * (by default, JAXB annotations are used as {@link Priority#PRIMARY}
 * annotations).
 */
public class JaxbAnnotationModule extends SimpleModule
{
    /**
     * Enumeration that defines how we use JAXB Annotations: either
     * as "primary" annotations (before any other already configured
     * introspector -- most likely default JacksonAnnotationIntrospector) or
     * as "secondary" annotations (after any other already configured
     * introspector(s)).
     *<p>
     * Default choice is <b>PRIMARY</b>
     *<p>
     * Note that if you want to use JAXB annotations as the only annotations,
     * you must directly set annotation introspector by calling 
     * {@link com.fasterxml.jackson.databind.ObjectMapper#setAnnotationIntrospector}.
     */
    public enum Priority {
        PRIMARY, SECONDARY;
    }
    
    /**
     * Priority to use when registering annotation introspector: default
     * value is {@link Priority#PRIMARY}.
     */
    protected Priority _priority = Priority.PRIMARY;

    /**
     * Setting to determine whether default behavior with JAXB
     * {@link javax.xml.bind.annotations.XmlIDXX}
     *<p>
     * The default value is <code>false</code>, for backwards compatibility
     * (since 2.0 only allowed this value)
     *
     * @since 2.1
     */
    protected boolean _firstXmlidRefAsId = false;

    /*
    /**********************************************************
    /* Life cycle
    /**********************************************************
     */
    
    public JaxbAnnotationModule()
    {
        super("jaxb-annotations", ModuleVersion.instance.version());
    }
    
    @Override
    public void setupModule(SetupContext context)
    {
        JaxbAnnotationIntrospector intr = new JaxbAnnotationIntrospector(context.getTypeFactory(),
                _firstXmlidRefAsId);
        switch (_priority) {
        case PRIMARY:
            context.insertAnnotationIntrospector(intr);
            break;
        case SECONDARY:
            context.appendAnnotationIntrospector(intr);
            break;
        }
    }

    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */
    
    /**
     * Method for defining whether JAXB annotations should be added
     * as primary or secondary annotations (compared to already registered
     * annotations).
     *<p>
     * NOTE: method MUST be called before registering the module -- calling
     * afterwards will not have any effect on previous registrations.
     */
    public JaxbAnnotationModule setPriority(Priority p) {
        _priority = p;
        return this;
    }

    /**
     * Method for definining whether to always use id value when serializing
     * something indicated with {@link XmlIdRef} or not: if not, then first instance
     * will be serialized as POJOs, and only later references using id.
     * If enabled, serialization will always use value of Object Id.
     * 
     * @param state of "first-ref-as-id" feature: true means "yes, always serialize
     *    reference as id", and true "serialize first reference normally, and other
     *    as id"
     * 
     * @return This module, to allow call chaining
     * @since 2.1
     */
    public JaxbAnnotationModule setFirstAsId(boolean state) {
        _firstXmlidRefAsId = state;
        return this;
    }
    
    
    public Priority getPriority() { return _priority; }
}
