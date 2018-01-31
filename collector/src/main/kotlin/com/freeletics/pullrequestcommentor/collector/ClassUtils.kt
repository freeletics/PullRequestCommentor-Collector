package com.freeletics.pullrequestcommentor.collector


/**
 * Maps primitive `Class`es to their corresponding wrapper `Class`.
 */
private val primitiveWrapperMap = HashMap<Class<*>, Class<*>>().also {

    it[java.lang.Boolean.TYPE] = Boolean::class.java
    it[java.lang.Byte.TYPE] = Byte::class.java
    it[Character.TYPE] = Char::class.java
    it[java.lang.Short.TYPE] = Short::class.java
    it[Integer.TYPE] = Int::class.java
    it[java.lang.Long.TYPE] = Long::class.java
    it[java.lang.Double.TYPE] = Double::class.java
    it[java.lang.Float.TYPE] = Float::class.java
    it[Void.TYPE] = Void.TYPE
}

/**
 * Maps wrapper `Class`es to their corresponding primitive types.
 */
private val wrapperPrimitiveMap = HashMap<Class<*>, Class<*>>().also {
    for ((primitiveClass, wrapperClass) in primitiveWrapperMap) {
        if (primitiveClass != wrapperClass) {
            it[wrapperClass] = primitiveClass
        }
    }
}


/**
 *
 * Converts the specified primitive Class object to its corresponding
 * wrapper Class object.
 *
 *
 * NOTE: From v2.2, this method handles `Void.TYPE`,
 * returning `Void.TYPE`.
 *
 * @param cls  the class to convert, may be null
 * @return the wrapper class for `cls` or `cls` if
 * `cls` is not a primitive. `null` if null input.
 * @since 2.1
 */
private fun primitiveToWrapper(cls: Class<*>?): Class<*>? {
    var convertedClass = cls
    if (cls != null && cls.isPrimitive) {
        convertedClass = primitiveWrapperMap[cls]
    }
    return convertedClass
}


/**
 *
 * Converts the specified wrapper class to its corresponding primitive
 * class.
 *
 *
 * This method is the counter part of `primitiveToWrapper()`.
 * If the passed in class is a wrapper class for a primitive type, this
 * primitive type will be returned (e.g. `Integer.TYPE` for
 * `Integer.class`). For other classes, or if the parameter is
 * **null**, the return value is **null**.
 *
 * @param cls the class to convert, may be **null**
 * @return the corresponding primitive type if `cls` is a
 * wrapper class, **null** otherwise
 * @see .primitiveToWrapper
 * @since 2.4
 */
fun wrapperToPrimitive(cls: Class<*>): Class<*>? {
    return wrapperPrimitiveMap[cls]
}

/**
 *
 * Checks if one `Class` can be assigned to a variable of
 * another `Class`.
 *
 *
 * Unlike the [Class.isAssignableFrom] method,
 * this method takes into account widenings of primitive classes and
 * `null`s.
 *
 *
 * Primitive widenings allow an int to be assigned to a long, float or
 * double. This method returns the correct result for these cases.
 *
 *
 * `Null` may be assigned to any reference type. This method
 * will return `true` if `null` is passed in and the
 * toClass is non-primitive.
 *
 *
 * Specifically, this method tests whether the type represented by the
 * specified `Class` parameter can be converted to the type
 * represented by this `Class` object via an identity conversion
 * widening primitive or widening reference conversion. See
 * *[The Java Language Specification](http://docs.oracle.com/javase/specs/)*,
 * sections 5.1.1, 5.1.2 and 5.1.4 for details.
 *
 * @param cls  the Class to check, may be null
 * @param toClass  the Class to try to assign into, returns false if null
 * @param autoboxing  whether to use implicit autoboxing/unboxing between primitives and wrappers
 * @return `true` if assignment possible
 */
internal fun Class<*>?.isAssignable(toClass: Class<*>?, autoboxing: Boolean): Boolean {
    var cls = this
    if (toClass == null) {
        return false
    }
    // have to check for null, as isAssignableFrom doesn't
    if (cls == null) {
        return !toClass.isPrimitive
    }
    //autoboxing:
    if (autoboxing) {
        if (cls.isPrimitive && !toClass.isPrimitive) {
            cls = primitiveToWrapper(cls)
            if (cls == null) {
                return false
            }
        }
        if (toClass.isPrimitive && !cls.isPrimitive) {
            cls = wrapperToPrimitive(cls)
            if (cls == null) {
                return false
            }
        }
    }
    if (cls == toClass) {
        return true
    }
    if (cls.isPrimitive) {
        if (!toClass.isPrimitive) {
            return false
        }
        if (Integer.TYPE == cls) {
            return (java.lang.Long.TYPE == toClass
                    || java.lang.Float.TYPE == toClass
                    || java.lang.Double.TYPE == toClass)
        }
        if (java.lang.Long.TYPE == cls) {
            return java.lang.Float.TYPE == toClass || java.lang.Double.TYPE == toClass
        }
        if (java.lang.Boolean.TYPE == cls) {
            return false
        }
        if (java.lang.Double.TYPE == cls) {
            return false
        }
        if (java.lang.Float.TYPE == cls) {
            return java.lang.Double.TYPE == toClass
        }
        if (Character.TYPE == cls) {
            return (Integer.TYPE == toClass
                    || java.lang.Long.TYPE == toClass
                    || java.lang.Float.TYPE == toClass
                    || java.lang.Double.TYPE == toClass)
        }
        if (java.lang.Short.TYPE == cls) {
            return (Integer.TYPE == toClass
                    || java.lang.Long.TYPE == toClass
                    || java.lang.Float.TYPE == toClass
                    || java.lang.Double.TYPE == toClass)
        }
        return if (java.lang.Byte.TYPE == cls) {
            (java.lang.Short.TYPE == toClass
                    || Integer.TYPE == toClass
                    || java.lang.Long.TYPE == toClass
                    || java.lang.Float.TYPE == toClass
                    || java.lang.Double.TYPE == toClass)
        } else false
        // should never get here
    }
    return toClass.isAssignableFrom(cls)
}

