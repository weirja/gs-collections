/*
 * Copyright 2013 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.list.mutable.primitive;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.NoSuchElementException;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.LazyBooleanIterable;
import com.gs.collections.api.bag.primitive.MutableBooleanBag;
import com.gs.collections.api.block.function.primitive.BooleanToObjectFunction;
import com.gs.collections.api.block.predicate.primitive.BooleanPredicate;
import com.gs.collections.api.block.procedure.primitive.BooleanProcedure;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.BooleanList;
import com.gs.collections.api.list.primitive.ImmutableBooleanList;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.api.set.primitive.MutableBooleanSet;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.lazy.primitive.LazyBooleanIterableAdapter;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import net.jcip.annotations.NotThreadSafe;

/**
 * BooleanArrayList is similar to {@link FastList}, and is memory-optimized for boolean primitives.
 */
@NotThreadSafe
public final class BooleanArrayList
        implements MutableBooleanList, Externalizable
{
    private static final long serialVersionUID = 1L;

    private int size;
    private transient BitSet items;

    public BooleanArrayList()
    {
    }

    public BooleanArrayList(int initialCapacity)
    {
        if (initialCapacity != 0)
        {
            this.items = new BitSet(initialCapacity);
        }
    }

    public BooleanArrayList(boolean... array)
    {
        this.size = array.length;
        this.items = new BitSet(array.length);
        for (int i = 0; i < array.length; i++)
        {
            if (array[i])
            {
                this.items.set(i);
            }
        }
    }

    private BooleanArrayList(BooleanIterable booleanIterable)
    {
        BooleanIterator booleanIterator = booleanIterable.booleanIterator();
        while (booleanIterator.hasNext())
        {
            this.add(booleanIterator.next());
        }
    }

    public static BooleanArrayList newListWith(boolean... array)
    {
        return new BooleanArrayList(array);
    }

    public static BooleanArrayList newList(BooleanIterable source)
    {
        return new BooleanArrayList(source);
    }

    public int size()
    {
        return this.size;
    }

    public boolean isEmpty()
    {
        return this.size == 0;
    }

    public boolean notEmpty()
    {
        return this.size > 0;
    }

    public void clear()
    {
        if (this.items != null)
        {
            this.items.clear();
            this.size = 0;
        }
    }

    public boolean contains(boolean value)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (this.items.get(i) == value)
            {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(boolean... source)
    {
        for (boolean value : source)
        {
            if (!this.contains(value))
            {
                return false;
            }
        }
        return true;
    }

    public boolean containsAll(BooleanIterable source)
    {
        for (BooleanIterator iterator = source.booleanIterator(); iterator.hasNext(); )
        {
            if (!this.contains(iterator.next()))
            {
                return false;
            }
        }
        return true;
    }

    public boolean get(int index)
    {
        if (index < this.size)
        {
            return this.items.get(index);
        }
        throw this.newIndexOutOfBoundsException(index);
    }

    private IndexOutOfBoundsException newIndexOutOfBoundsException(int index)
    {
        return new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size);
    }

    public boolean getFirst()
    {
        this.checkEmpty();
        return this.items.get(0);
    }

    public boolean getLast()
    {
        this.checkEmpty();
        return this.items.get(this.size() - 1);
    }

    private void checkEmpty()
    {
        if (this.isEmpty())
        {
            throw this.newIndexOutOfBoundsException(0);
        }
    }

    public int indexOf(boolean object)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (this.items.get(i) == object)
            {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexOf(boolean object)
    {
        for (int i = this.size - 1; i >= 0; i--)
        {
            if (this.items.get(i) == object)
            {
                return i;
            }
        }
        return -1;
    }

    public boolean add(boolean newItem)
    {
        if (this.size == 0)
        {
            this.items = new BitSet();
        }
        if (newItem)
        {
            this.items.set(this.size);
        }
        this.size++;
        return true;
    }

    public boolean addAll(boolean... source)
    {
        if (source.length < 1)
        {
            return false;
        }

        for (boolean sourceItem : source)
        {
            this.add(sourceItem);
        }
        return true;
    }

    public boolean addAll(BooleanIterable source)
    {
        return this.addAll(source.toArray());
    }

    public void addAtIndex(int index, boolean element)
    {
        if (index > -1 && index < this.size)
        {
            this.addAtIndexLessThanSize(index, element);
        }
        else if (index == this.size)
        {
            this.add(element);
        }
        else
        {
            throw this.newIndexOutOfBoundsException(index);
        }
    }

    private void addAtIndexLessThanSize(int index, boolean element)
    {
        for (int i = this.size + 1; i > index; i--)
        {
            this.items.set(i, this.items.get(i - 1));
        }
        this.items.set(index, element);
        this.size++;
    }

    public boolean addAllAtIndex(int index, boolean... source)
    {
        if (index > this.size || index < 0)
        {
            throw this.newIndexOutOfBoundsException(index);
        }
        if (source.length == 0)
        {
            return false;
        }
        int sourceSize = source.length;
        int newSize = this.size + sourceSize;

        for (int i = newSize; i > index; i--)
        {
            this.items.set(i, this.items.get(i - sourceSize));
        }

        for (int i = 0; i < sourceSize; i++)
        {
            this.items.set(i + index, source[i]);
        }

        this.size = newSize;
        return true;
    }

    public boolean addAllAtIndex(int index, BooleanIterable source)
    {
        return this.addAllAtIndex(index, source.toArray());
    }

    public boolean remove(boolean value)
    {
        int index = this.indexOf(value);
        if (index >= 0)
        {
            this.removeAtIndex(index);
            return true;
        }
        return false;
    }

    public boolean removeAll(BooleanIterable source)
    {
        boolean modified = false;
        BooleanIterator iterator = source.booleanIterator();
        while (iterator.hasNext())
        {
            boolean item = iterator.next();
            if (this.remove(item))
            {
                modified = true;
            }
        }
        return modified;
    }

    public boolean removeAll(boolean... source)
    {
        boolean modified = false;
        for (boolean i : source)
        {
            modified = this.remove(i);
        }
        return modified;
    }

    public boolean removeAtIndex(int index)
    {
        boolean previous = this.get(index);
        if (this.size - index > 1)
        {
            for (int i = index; i < this.size; i++)
            {
                this.items.set(i, this.items.get(i + 1));
            }
        }
        --this.size;
        this.items.clear(this.size);
        return previous;
    }

    public boolean set(int index, boolean element)
    {
        boolean previous = this.get(index);
        this.items.set(index, element);
        return previous;
    }

    public BooleanArrayList with(boolean element)
    {
        this.add(element);
        return this;
    }

    public BooleanArrayList without(boolean element)
    {
        this.remove(element);
        return this;
    }

    public BooleanArrayList withAll(BooleanIterable elements)
    {
        this.addAll(elements.toArray());
        return this;
    }

    public BooleanArrayList withoutAll(BooleanIterable elements)
    {
        this.removeAll(elements);
        return this;
    }

    public BooleanArrayList with(boolean element1, boolean element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public BooleanArrayList with(boolean element1, boolean element2, boolean element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public BooleanArrayList with(boolean element1, boolean element2, boolean element3, boolean... elements)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        this.addAll(elements);
        return this;
    }

    public BooleanIterator booleanIterator()
    {
        return new InternalBooleanIterator();
    }

    public void forEach(BooleanProcedure procedure)
    {
        for (int i = 0; i < this.size; i++)
        {
            procedure.value(this.items.get(i));
        }
    }

    public int count(BooleanPredicate predicate)
    {
        int count = 0;
        for (int i = 0; i < this.size; i++)
        {
            if (predicate.accept(this.items.get(i)))
            {
                count++;
            }
        }
        return count;
    }

    public boolean anySatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (predicate.accept(this.items.get(i)))
            {
                return true;
            }
        }
        return false;
    }

    public boolean allSatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (!predicate.accept(this.items.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    public boolean noneSatisfy(BooleanPredicate predicate)
    {
        for (int i = 0; i < this.size; i++)
        {
            if (predicate.accept(this.items.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    public BooleanArrayList select(BooleanPredicate predicate)
    {
        BooleanArrayList result = new BooleanArrayList();
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            if (predicate.accept(item))
            {
                result.add(item);
            }
        }
        return result;
    }

    public BooleanArrayList reject(BooleanPredicate predicate)
    {
        BooleanArrayList result = new BooleanArrayList();
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            if (!predicate.accept(item))
            {
                result.add(item);
            }
        }
        return result;
    }

    public BooleanIterable asReversed()
    {
        return new ReverseBooleanIterable();
    }

    public BooleanArrayList reverseThis()
    {
        int endIndex = this.size - 1;
        for (int i = 0; i < this.size / 2; i++)
        {
            boolean tempSwapValue = this.items.get(i);
            this.items.set(i, this.items.get(endIndex - i));
            this.items.set(endIndex - i, tempSwapValue);
        }
        return this;
    }

    public MutableBooleanList asUnmodifiable()
    {
        throw new UnsupportedOperationException("asUnmodifiable not implemented yet");
    }

    public MutableBooleanList asSynchronized()
    {
        throw new UnsupportedOperationException("asSynchronized not implemented yet");
    }

    public ImmutableBooleanList toImmutable()
    {
        throw new UnsupportedOperationException("toImmutable not implemented yet");
    }

    public BooleanArrayList toReversed()
    {
        return new BooleanArrayList(this.asReversed());
    }

    public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
    {
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            if (predicate.accept(item))
            {
                return item;
            }
        }
        return ifNone;
    }

    public <V> MutableList<V> collect(BooleanToObjectFunction<? extends V> function)
    {
        FastList<V> target = FastList.newList(this.size);
        for (int i = 0; i < this.size; i++)
        {
            target.add(function.valueOf(this.items.get(i)));
        }
        return target;
    }

    public boolean[] toArray()
    {
        boolean[] newItems = new boolean[this.size];
        for (int i = 0; i < this.size; i++)
        {
            newItems[i] = this.items.get(i);
        }
        return newItems;
    }

    @Override
    public boolean equals(Object otherList)
    {
        if (otherList == this)
        {
            return true;
        }
        if (!(otherList instanceof BooleanList))
        {
            return false;
        }
        BooleanList list = (BooleanList) otherList;
        if (this.size != list.size())
        {
            return false;
        }
        for (int i = 0; i < this.size; i++)
        {
            if (this.items.get(i) != list.get(i))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (int i = 0; i < this.size; i++)
        {
            boolean item = this.items.get(i);
            hashCode = 31 * hashCode + (item ? 1231 : 1237);
        }
        return hashCode;
    }

    @Override
    public String toString()
    {
        return this.makeString("[", ", ", "]");
    }

    public String makeString()
    {
        return this.makeString(", ");
    }

    public String makeString(String separator)
    {
        return this.makeString("", separator, "");
    }

    public String makeString(String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        this.appendString(stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    public void appendString(Appendable appendable)
    {
        this.appendString(appendable, ", ");
    }

    public void appendString(Appendable appendable, String separator)
    {
        this.appendString(appendable, "", separator, "");
    }

    public void appendString(
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        try
        {
            appendable.append(start);
            for (int i = 0; i < this.size; i++)
            {
                if (i > 0)
                {
                    appendable.append(separator);
                }
                boolean value = this.items.get(i);
                appendable.append(String.valueOf(value));
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public MutableBooleanList toList()
    {
        return BooleanArrayList.newList(this);
    }

    public MutableBooleanSet toSet()
    {
        return BooleanHashSet.newSet(this);
    }

    public MutableBooleanBag toBag()
    {
        return BooleanHashBag.newBag(this);
    }

    public LazyBooleanIterable asLazy()
    {
        return new LazyBooleanIterableAdapter(this);
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());
        for (int i = 0; i < this.size; i++)
        {
            out.writeBoolean(this.items.get(i));
        }
    }

    public void readExternal(ObjectInput in) throws IOException
    {
        this.size = in.readInt();
        if (this.size > 0)
        {
            this.items = new BitSet();
            for (int i = 0; i < this.size; i++)
            {
                this.items.set(i, in.readBoolean());
            }
        }
    }

    private class InternalBooleanIterator implements BooleanIterator
    {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int currentIndex;

        public boolean hasNext()
        {
            return this.currentIndex != BooleanArrayList.this.size();
        }

        public boolean next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            boolean next = BooleanArrayList.this.get(this.currentIndex);
            this.currentIndex++;
            return next;
        }
    }

    private class ReverseBooleanIterable implements BooleanIterable
    {
        public BooleanIterator booleanIterator()
        {
            return new ReverseBooleanIterator();
        }

        public boolean containsAll(BooleanIterable source)
        {
            for (BooleanIterator iterator = source.booleanIterator(); iterator.hasNext(); )
            {
                if (!this.contains(iterator.next()))
                {
                    return false;
                }
            }
            return true;
        }

        public LazyBooleanIterable asLazy()
        {
            return new LazyBooleanIterableAdapter(this);
        }

        public void forEach(BooleanProcedure procedure)
        {
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                procedure.value(iterator.next());
            }
        }

        public int count(BooleanPredicate predicate)
        {
            int count = 0;
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                if (predicate.accept(iterator.next()))
                {
                    count++;
                }
            }
            return count;
        }

        public boolean anySatisfy(BooleanPredicate predicate)
        {
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                if (predicate.accept(iterator.next()))
                {
                    return true;
                }
            }
            return false;
        }

        public boolean allSatisfy(BooleanPredicate predicate)
        {
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                if (!predicate.accept(iterator.next()))
                {
                    return false;
                }
            }
            return true;
        }

        public boolean noneSatisfy(BooleanPredicate predicate)
        {
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                if (predicate.accept(iterator.next()))
                {
                    return false;
                }
            }
            return true;
        }

        public BooleanIterable select(BooleanPredicate predicate)
        {
            BooleanArrayList result = new BooleanArrayList(BooleanArrayList.this.size);
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                boolean value = iterator.next();
                if (predicate.accept(value))
                {
                    result.add(value);
                }
            }
            return result;
        }

        public BooleanIterable reject(BooleanPredicate predicate)
        {
            BooleanArrayList result = new BooleanArrayList(BooleanArrayList.this.size);
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                boolean value = iterator.next();
                if (!predicate.accept(value))
                {
                    result.add(value);
                }
            }
            return result;
        }

        public boolean detectIfNone(BooleanPredicate predicate, boolean ifNone)
        {
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                boolean value = iterator.next();
                if (predicate.accept(value))
                {
                    return value;
                }
            }
            return ifNone;
        }

        public <V> MutableList<V> collect(BooleanToObjectFunction<? extends V> function)
        {
            MutableList<V> results = FastList.newList(BooleanArrayList.this.size);
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                results.add(function.valueOf(iterator.next()));
            }
            return results;
        }

        public boolean[] toArray()
        {
            boolean[] results = new boolean[BooleanArrayList.this.size];
            int index = 0;
            BooleanIterator iterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                results[index] = iterator.next();
                index++;
            }
            return results;
        }

        public boolean contains(boolean value)
        {
            return BooleanArrayList.this.contains(value);
        }

        public boolean containsAll(boolean... source)
        {
            return BooleanArrayList.this.containsAll(source);
        }

        public int size()
        {
            return BooleanArrayList.this.size;
        }

        public boolean isEmpty()
        {
            return BooleanArrayList.this.isEmpty();
        }

        public boolean notEmpty()
        {
            return BooleanArrayList.this.notEmpty();
        }

        @Override
        public String toString()
        {
            return this.makeString("[", ", ", "]");
        }

        public String makeString()
        {
            return this.makeString(", ");
        }

        public String makeString(String separator)
        {
            return this.makeString("", separator, "");
        }

        public String makeString(String start, String separator, String end)
        {
            Appendable stringBuilder = new StringBuilder();
            this.appendString(stringBuilder, start, separator, end);
            return stringBuilder.toString();
        }

        public void appendString(Appendable appendable)
        {
            this.appendString(appendable, ", ");
        }

        public void appendString(Appendable appendable, String separator)
        {
            this.appendString(appendable, "", separator, "");
        }

        public void appendString(Appendable appendable, String start, String separator, String end)
        {
            try
            {
                appendable.append(start);
                BooleanIterator iterator = this.booleanIterator();
                boolean appendSeparator = false;
                while (iterator.hasNext())
                {
                    if (appendSeparator)
                    {
                        appendable.append(separator);
                    }
                    appendable.append(String.valueOf(iterator.next()));
                    appendSeparator = true;
                }
                appendable.append(end);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        public MutableBooleanList toList()
        {
            return BooleanArrayList.newList(this);
        }

        public MutableBooleanSet toSet()
        {
            return BooleanHashSet.newSet(this);
        }

        public MutableBooleanBag toBag()
        {
            return BooleanHashBag.newBag(this);
        }

        @Override
        public int hashCode()
        {
            return BooleanArrayList.this.hashCode();
        }

        @Override
        public boolean equals(Object otherIterable)
        {
            if (otherIterable == this)
            {
                return true;
            }
            if (!(otherIterable instanceof ReverseBooleanIterable))
            {
                return false;
            }
            ReverseBooleanIterable reverseBooleanIterable = (ReverseBooleanIterable) otherIterable;
            if (this.size() != reverseBooleanIterable.size())
            {
                return false;
            }
            BooleanIterator iterator = this.booleanIterator();
            BooleanIterator otherIterator = this.booleanIterator();
            while (iterator.hasNext())
            {
                if (iterator.next() != otherIterator.next())
                {
                    return false;
                }
            }
            return true;
        }

        private class ReverseBooleanIterator implements BooleanIterator
        {
            /**
             * Index of element to be returned by subsequent call to next.
             */
            private int currentIndex = BooleanArrayList.this.size - 1;

            public boolean hasNext()
            {
                return this.currentIndex != -1;
            }

            public boolean next()
            {
                try
                {
                    boolean next = BooleanArrayList.this.get(this.currentIndex);
                    this.currentIndex--;
                    return next;
                }
                catch (IndexOutOfBoundsException ignored)
                {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}
