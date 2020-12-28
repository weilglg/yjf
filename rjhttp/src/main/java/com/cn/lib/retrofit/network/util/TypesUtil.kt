/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.retrofit.network.util

import java.io.Serializable
import java.lang.reflect.*
import java.util.*


/**
 * Static methods for working with types.
 *
 * @author Bob Lee
 * @author Jesse Wilson
 */
class TypesUtil private constructor() {

    init {
        throw UnsupportedOperationException()
    }

    private class ParameterizedTypeImpl(ownerType: Type? = null, rawType: Type, vararg typeArguments: Type?) : ParameterizedType, Serializable {
        private val ownerType: Type?
        private val rawType: Type
        private val typeArguments: Array<Type>

        init {
            // require an owner type if the raw type needs it
            if (rawType is Class<*>) {
                val rawTypeAsClass = rawType
                val isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.modifiers) || rawTypeAsClass.enclosingClass == null
                checkArgument(ownerType != null || isStaticOrTopLevelClass)
            }

            this.ownerType = if (ownerType == null) null else canonicalize(ownerType)
            this.rawType = canonicalize(rawType)
            this.typeArguments = typeArguments.clone() as Array<Type>
            var t = 0
            val length = this.typeArguments.size
            while (t < length) {
                checkNotNull<Type>(this.typeArguments[t])
                checkNotPrimitive(this.typeArguments[t])
                this.typeArguments[t] = canonicalize(this.typeArguments[t])
                t++
            }
        }

        override fun getActualTypeArguments(): Array<Type> {
            return typeArguments.clone()
        }

        override fun getRawType(): Type {
            return rawType
        }

        override fun getOwnerType(): Type? {
            return ownerType
        }

        override fun equals(other: Any?): Boolean {
            return other is ParameterizedType && TypesUtil.equals(this, other)
        }

        override fun hashCode(): Int {
            return (Arrays.hashCode(typeArguments)
                    xor rawType.hashCode()
                    xor hashCodeOrZero(ownerType))
        }

        override fun toString(): String {
            val length = typeArguments.size
            if (length == 0) {
                return typeToString(rawType)
            }

            val stringBuilder = StringBuilder(30 * (length + 1))
            stringBuilder.append(typeToString(rawType)).append("<").append(typeToString(typeArguments[0]))
            for (i in 1 until length) {
                stringBuilder.append(", ").append(typeToString(typeArguments[i]))
            }
            return stringBuilder.append(">").toString()
        }

        companion object {

            private const val serialVersionUID: Long = 0
        }
    }

    private class GenericArrayTypeImpl(componentType: Type) : GenericArrayType, Serializable {
        private val componentType: Type = canonicalize(componentType)

        override fun getGenericComponentType(): Type {
            return componentType
        }

        override fun equals(o: Any?): Boolean {
            return o is GenericArrayType && TypesUtil.equals(this, o)
        }

        override fun hashCode(): Int {
            return componentType.hashCode()
        }

        override fun toString(): String {
            return typeToString(componentType) + "[]"
        }

        companion object {

            private const val serialVersionUID: Long = 0
        }
    }

    /**
     * The WildcardType interface supports multiple upper bounds and multiple
     * lower bounds. We only support what the Java 6 language needs - at most one
     * bound. If a lower bound is set, the upper bound must be Object.class.
     */
    private class WildcardTypeImpl(upperBounds: Array<Type>, lowerBounds: Array<Type>) : WildcardType, Serializable {
        private val upperBound: Type
        private val lowerBound: Type?

        init {
            checkArgument(lowerBounds.size <= 1)
            checkArgument(upperBounds.size == 1)

            if (lowerBounds.size == 1) {
                checkNotNull<Type>(lowerBounds[0])
                checkNotPrimitive(lowerBounds[0])
                checkArgument(upperBounds[0] === Any::class.java)
                this.lowerBound = canonicalize(lowerBounds[0])
                this.upperBound = Any::class.java

            } else {
                checkNotNull<Type>(upperBounds[0])
                checkNotPrimitive(upperBounds[0])
                this.lowerBound = null
                this.upperBound = canonicalize(upperBounds[0])
            }
        }

        override fun getUpperBounds(): Array<Type> {
            return arrayOf<Type>(upperBound)
        }

        override fun getLowerBounds(): Array<Type> {
            return if (lowerBound != null) arrayOf<Type>(lowerBound) else EMPTY_TYPE_ARRAY
        }

        override fun equals(other: Any?): Boolean {
            return other is WildcardType && TypesUtil.equals(this, other)
        }

        override fun hashCode(): Int {
            // this equals Arrays.hashCode(getLowerBounds()) ^ Arrays.hashCode(getUpperBounds());
            return (if (lowerBound != null) 31 + lowerBound.hashCode() else 1) xor 31 + upperBound.hashCode()
        }

        override fun toString(): String {
            return if (lowerBound != null) {
                "? super " + typeToString(lowerBound)
            } else if (upperBound === Any::class.java) {
                "?"
            } else {
                "? extends " + typeToString(upperBound)
            }
        }

        companion object {

            private const val serialVersionUID: Long = 0
        }
    }

    companion object {
        internal val EMPTY_TYPE_ARRAY = arrayOf<Type>()

        /**
         * Returns a new parameterized type, applying `typeArguments` to
         * `rawType` and enclosed by `ownerType`.
         *
         * @return a [serializable][Serializable] parameterized type.
         */
        fun newParameterizedTypeWithOwner(ownerType: Type? = null, rawType: Type, vararg typeArguments: Type?): ParameterizedType {
            return ParameterizedTypeImpl(ownerType, rawType, *typeArguments)
        }

        /**
         * Returns an array type whose elements are all instances of
         * `componentType`.
         *
         * @return a [serializable][Serializable] generic array type.
         */
        fun arrayOf(componentType: Type): GenericArrayType {
            return GenericArrayTypeImpl(componentType)
        }

        /**
         * Returns a type that represents an unknown type that extends `bound`.
         * For example, if `bound` is `CharSequence.class`, this returns
         * `? extends CharSequence`. If `bound` is `Object.class`,
         * this returns `?`, which is shorthand for `? extends Object`.
         */
        fun subtypeOf(bound: Type): WildcardType {
            val upperBounds: Array<Type>
            if (bound is WildcardType) {
                upperBounds = bound.upperBounds
            } else {
                upperBounds = arrayOf<Type>(bound)
            }
            return WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY)
        }

        /**
         * Returns a type that represents an unknown supertype of `bound`. For
         * example, if `bound` is `String.class`, this returns `?
         * super String`.
         */
        fun supertypeOf(bound: Type): WildcardType {
            val lowerBounds: Array<Type>
            if (bound is WildcardType) {
                lowerBounds = bound.lowerBounds
            } else {
                lowerBounds = arrayOf<Type>(bound)
            }
            return WildcardTypeImpl(arrayOf<Type>(Any::class.java), lowerBounds)
        }

        /**
         * Returns a type that is functionally equal but not necessarily equal
         * according to [Object.equals()][Object.equals]. The returned
         * type is [Serializable].
         */
        fun canonicalize(type: Type): Type {
            return if (type is Class<*>) {
                if (type.isArray) GenericArrayTypeImpl(canonicalize(type.componentType)) else type

            } else if (type is ParameterizedType) {
                ParameterizedTypeImpl(type.ownerType,
                        type.rawType, *type.actualTypeArguments)

            } else if (type is GenericArrayType) {
                GenericArrayTypeImpl(type.genericComponentType)

            } else if (type is WildcardType) {
                WildcardTypeImpl(type.upperBounds, type.lowerBounds)

            } else {
                // type is either serializable as-is or unsupported
                type
            }
        }

        fun getRawType(type: Type?): Class<*> {
            if (type is Class<*>) {
                // type is a normal class.
                return type

            } else if (type is ParameterizedType) {
                val parameterizedType = type as ParameterizedType?

                // I'm not exactly sure why getRawType() returns Type instead of Class.
                // Neal isn't either but suspects some pathological case related
                // to nested classes exists.
                val rawType = parameterizedType!!.rawType
                checkArgument(rawType is Class<*>)
                return rawType as Class<*>

            } else if (type is GenericArrayType) {
                val componentType = type.genericComponentType
                return java.lang.reflect.Array.newInstance(getRawType(componentType), 0).javaClass

            } else if (type is TypeVariable<*>) {
                // we could use the variable's bounds, but that won't work if there are multiple.
                // having a raw type that's more general than necessary is okay
                return Any::class.java

            } else if (type is WildcardType) {
                return getRawType(type.upperBounds[0])

            } else {
                val className = type?.javaClass?.getName() ?: "null"
                throw IllegalArgumentException("Expected a Class, ParameterizedType, or "
                        + "GenericArrayType, but <" + type + "> is of type " + className)
            }
        }

        internal fun equal(a: Any?, b: Any): Boolean {
            return a === b || a != null && a == b
        }

        /**
         * Returns true if `a` and `b` are equal.
         */
        fun equals(a: Type, b: Type): Boolean {
            return if (a === b) {
                // also handles (a == null && b == null)
                true

            } else if (a is Class<*>) {
                // Class already specifies equals().
                a == b

            } else if (a is ParameterizedType) {
                if (b !is ParameterizedType) {
                    false
                } else equal(a.ownerType, b.ownerType)
                        && a.rawType == b.rawType
                        && Arrays.equals(a.actualTypeArguments, b.actualTypeArguments)

            } else if (a is GenericArrayType) {
                if (b !is GenericArrayType) {
                    false
                } else equals(a.genericComponentType, b.genericComponentType)

            } else if (a is WildcardType) {
                if (b !is WildcardType) {
                    false
                } else Arrays.equals(a.upperBounds, b.upperBounds) && Arrays.equals(a.lowerBounds, b.lowerBounds)

            } else if (a is TypeVariable<*>) {
                if (b !is TypeVariable<*>) {
                    false
                } else a.genericDeclaration === b.genericDeclaration && a.name == b.name

            } else {
                // This isn't a type we support. Could be a generic array type, wildcard type, etc.
                false
            }
        }

        internal fun hashCodeOrZero(o: Any?): Int {
            return o?.hashCode() ?: 0
        }

        fun typeToString(type: Type): String {
            return if (type is Class<*>) type.name else type.toString()
        }

        /**
         * Returns the generic supertype for `supertype`. For example, given a class `IntegerSet`, the result for when supertype is `Set.class` is `Set<Integer>` and the
         * result when the supertype is `Collection.class` is `Collection<Integer>`.
         */
        internal fun getGenericSupertype(context: Type, rawType: Class<*>, toResolve: Class<*>): Type {
            var rawType = rawType
            if (toResolve == rawType) {
                return context
            }

            // we skip searching through interfaces if unknown is an interface
            if (toResolve.isInterface) {
                val interfaces = rawType.interfaces
                var i = 0
                val length = interfaces.size
                while (i < length) {
                    if (interfaces[i] == toResolve) {
                        return rawType.genericInterfaces[i]
                    } else if (toResolve.isAssignableFrom(interfaces[i])) {
                        return getGenericSupertype(rawType.genericInterfaces[i], interfaces[i], toResolve)
                    }
                    i++
                }
            }

            // check our supertypes
            if (!rawType.isInterface) {
                while (rawType != Any::class.java) {
                    val rawSupertype = rawType.superclass
                    if (rawSupertype == toResolve) {
                        return rawType.genericSuperclass
                    } else if (toResolve.isAssignableFrom(rawSupertype)) {
                        return getGenericSupertype(rawType.genericSuperclass, rawSupertype, toResolve)
                    }
                    rawType = rawSupertype
                }
            }

            // we can't resolve this further
            return toResolve
        }

        /**
         * Returns the generic form of `supertype`. For example, if this is `ArrayList<String>`, this returns `Iterable<String>` given the input `Iterable.class`.
         *
         * @param supertype a superclass of, or interface implemented by, this.
         */
        internal fun getSupertype(context: Type, contextRawType: Class<*>, supertype: Class<*>): Type {
            var context = context
            if (context is WildcardType) {
                // wildcards are useless for resolving supertypes. As the upper bound has the same raw type, use it instead
                context = context.upperBounds[0]
            }
            checkArgument(supertype.isAssignableFrom(contextRawType))
            return resolve(context, contextRawType,
                    TypesUtil.getGenericSupertype(context, contextRawType, supertype))
        }

        /**
         * Returns the component type of this array type.
         * @throws ClassCastException if this type is not an array.
         */
        fun getArrayComponentType(array: Type): Type {
            return if (array is GenericArrayType)
                array.genericComponentType
            else
                (array as Class<*>).componentType
        }

        /**
         * Returns the element type of this collection type.
         * @throws IllegalArgumentException if this type is not a collection.
         */
        fun getCollectionElementType(context: Type, contextRawType: Class<*>): Type {
            var collectionType = getSupertype(context, contextRawType, Collection::class.java)

            if (collectionType is WildcardType) {
                collectionType = collectionType.upperBounds[0]
            }
            return if (collectionType is ParameterizedType) {
                collectionType.actualTypeArguments[0]
            } else Any::class.java
        }

        /**
         * Returns a two element array containing this map's key and value types in
         * positions 0 and 1 respectively.
         */
        fun getMapKeyAndValueTypes(context: Type, contextRawType: Class<*>): Array<Type> {
            /*
     * Work around a problem with the declaration of java.util.Properties. That
     * class should extend Hashtable<String, String>, but it's declared to
     * extend Hashtable<Object, Object>.
     */
            if (context === Properties::class.java) {
                return arrayOf<Type>(String::class.java, String::class.java)
            }

            val mapType = getSupertype(context, contextRawType, Map::class.java)

            return if (mapType is ParameterizedType) {
                mapType.actualTypeArguments
            } else arrayOf<Type>(Any::class.java, Any::class.java)
        }

        fun resolve(context: Type, contextRawType: Class<*>, toResolve: Type): Type {
            return resolve(context, contextRawType, toResolve, HashSet())
        }

        private fun resolve(context: Type, contextRawType: Class<*>, toResolve: Type,
                            visitedTypeVariables: MutableCollection<TypeVariable<*>>): Type {
            var toResolve = toResolve
            // this implementation is made a little more complicated in an attempt to avoid object-creation
            while (true) {
                if (toResolve is TypeVariable<*>) {
                    val typeVariable = toResolve
                    if (visitedTypeVariables.contains(typeVariable)) {
                        // cannot reduce due to infinite recursion
                        return toResolve
                    } else {
                        visitedTypeVariables.add(typeVariable)
                    }
                    toResolve = resolveTypeVariable(context, contextRawType, typeVariable)
                    if (toResolve === typeVariable) {
                        return toResolve
                    }

                } else if (toResolve is Class<*> && toResolve.isArray) {
                    val original = toResolve
                    val componentType = original.componentType
                    val newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables)
                    return if (componentType === newComponentType)
                        original
                    else
                        arrayOf(newComponentType)

                } else if (toResolve is GenericArrayType) {
                    val original = toResolve
                    val componentType = original.genericComponentType
                    val newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables)
                    return if (componentType === newComponentType)
                        original
                    else
                        arrayOf(newComponentType)

                } else if (toResolve is ParameterizedType) {
                    val original = toResolve
                    val ownerType = original.ownerType
                    val newOwnerType = resolve(context, contextRawType, ownerType, visitedTypeVariables)
                    var changed = newOwnerType !== ownerType

                    var args = original.actualTypeArguments
                    var t = 0
                    val length = args.size
                    while (t < length) {
                        val resolvedTypeArgument = resolve(context, contextRawType, args[t], visitedTypeVariables)
                        if (resolvedTypeArgument !== args[t]) {
                            if (!changed) {
                                args = args.clone()
                                changed = true
                            }
                            args[t] = resolvedTypeArgument
                        }
                        t++
                    }

                    return if (changed)
                        newParameterizedTypeWithOwner(newOwnerType, original.rawType, *args)
                    else
                        original

                } else if (toResolve is WildcardType) {
                    val original = toResolve
                    val originalLowerBound = original.lowerBounds
                    val originalUpperBound = original.upperBounds

                    if (originalLowerBound.size == 1) {
                        val lowerBound = resolve(context, contextRawType, originalLowerBound[0], visitedTypeVariables)
                        if (lowerBound !== originalLowerBound[0]) {
                            return supertypeOf(lowerBound)
                        }
                    } else if (originalUpperBound.size == 1) {
                        val upperBound = resolve(context, contextRawType, originalUpperBound[0], visitedTypeVariables)
                        if (upperBound !== originalUpperBound[0]) {
                            return subtypeOf(upperBound)
                        }
                    }
                    return original

                } else {
                    return toResolve
                }
            }
        }

        internal fun resolveTypeVariable(context: Type, contextRawType: Class<*>, unknown: TypeVariable<*>): Type {
            val declaredByRaw = declaringClassOf(unknown) ?: return unknown

            // we can't reduce this further

            val declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw)
            if (declaredBy is ParameterizedType) {
                val index = indexOf(arrayOf(declaredByRaw.typeParameters), unknown)
                return declaredBy.actualTypeArguments[index]
            }

            return unknown
        }

        private fun indexOf(array: kotlin.Array<Any>, toFind: Any): Int {
            var i = 0
            val length = array.size
            while (i < length) {
                if (toFind == array[i]) {
                    return i
                }
                i++
            }
            throw NoSuchElementException()
        }

        /**
         * Returns the declaring class of `typeVariable`, or `null` if it was not declared by
         * a class.
         */
        private fun declaringClassOf(typeVariable: TypeVariable<*>): Class<*>? {
            val genericDeclaration = typeVariable.genericDeclaration
            return genericDeclaration as? Class<*>
        }

        internal fun checkNotPrimitive(type: Type) {
            checkArgument(type !is Class<*> || !type.isPrimitive)
        }

        fun <T> checkNotNull(obj: T?): T {
            if (obj == null) {
                throw NullPointerException()
            }
            return obj
        }

        fun checkArgument(condition: Boolean) {
            if (!condition) {
                throw IllegalArgumentException()
            }
        }
    }

}
