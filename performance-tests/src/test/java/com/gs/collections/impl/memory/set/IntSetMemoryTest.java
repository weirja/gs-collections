/*
 * Copyright 2012 Goldman Sachs.
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

package com.gs.collections.impl.memory.set;

import java.util.HashSet;

import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.procedure.primitive.IntProcedure;
import com.gs.collections.api.list.primitive.IntList;
import com.gs.collections.impl.memory.MemoryTestBench;
import com.gs.collections.impl.memory.TestDataFactory;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;
import gnu.trove.set.hash.TIntHashSet;
import org.junit.Test;

public class IntSetMemoryTest
{
    @Test
    public void memoryForScaledSets()
    {
        this.memoryForScaledSets(0);
        this.memoryForScaledSets(10);
        this.memoryForScaledSets(50);
        this.memoryForScaledSets(100);
        this.memoryForScaledSets(500);
        this.memoryForScaledSets(1000);
        this.memoryForScaledSets(5000);
        this.memoryForScaledSets(10000);
        this.memoryForScaledSets(50000);
        this.memoryForScaledSets(100000);
        this.memoryForScaledSets(500000);
        this.memoryForScaledSets(1000000);
    }

    public void memoryForScaledSets(int size)
    {
        MemoryTestBench.on(TIntHashSet.class)
                .printContainerMemoryUsage("IntSet", size, new TIntHashSetFactory(size));
        MemoryTestBench.on(IntHashSet.class)
                .printContainerMemoryUsage("IntSet", size, new IntHashSetFactory(size));
        MemoryTestBench.on(HashSet.class)
                .printContainerMemoryUsage("IntSet", size, new IntegerHashSetFactory(size));
    }

    public static class IntHashSetFactory implements Function0<IntHashSet>
    {
        private final IntList data;

        public IntHashSetFactory(int size)
        {
            this.data = TestDataFactory.create(size);
        }

        public IntHashSet value()
        {
            final IntHashSet set = new IntHashSet();
            this.data.forEach(new IntProcedure()
            {
                public void value(int each)
                {
                    set.add(each);
                }
            });
            return set;
        }
    }

    public static class TIntHashSetFactory implements Function0<TIntHashSet>
    {
        private final IntList data;

        public TIntHashSetFactory(int size)
        {
            this.data = TestDataFactory.create(size);
        }

        public TIntHashSet value()
        {
            final TIntHashSet set = new TIntHashSet();
            this.data.forEach(new IntProcedure()
            {
                public void value(int each)
                {
                    set.add(each);
                }
            });
            return set;
        }
    }

    public static class IntegerHashSetFactory implements Function0<HashSet<Integer>>
    {
        private final IntList data;

        public IntegerHashSetFactory(int size)
        {
            this.data = TestDataFactory.create(size);
        }

        public HashSet<Integer> value()
        {
            final HashSet<Integer> set = new HashSet<Integer>();
            this.data.forEach(new IntProcedure()
            {
                public void value(int each)
                {
                    set.add(each);
                }
            });
            return set;
        }
    }
}
