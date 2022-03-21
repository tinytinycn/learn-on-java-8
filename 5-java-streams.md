# 流式编程

> 集合优化了对象的存储，流Streams 则是关于一组对象的处理。

流（Streams）是与任何特定存储机制无关的元素序列。使用流即可从管道中提取元素并对其进行操作。这些管道通常被串联在一起形成一整套的管线， 来对流进行操作。取代了在集合中迭代元素的做法。

## 流支持 Streams Support

Java 设计者面临着这样一个难题：现存的大量类库不仅为 Java 所用，同时也被应用在整个 Java 生态圈数百万行的代码中。如何将一个全新的流的概念融入到现有类库中呢？

Java 8 采用的解决方案是：在接口interface中添加default默认方法。可以将Stream流式平滑地嵌入到现有类中。流操作的类型有三种：创建流、修改流元素（中间操作）、消费流元素（终端操作）。

### 1. 流创建

- Stream.of()。
- 每个集合可以调用stream()方法产生一个流。
- Stream.generate() 搭配 Supplier<T>。
- Stream.iterate() 产生的流的第一个元素是种子（iterate方法的第一个参数），然后将种子传递给方法（iterate方法的第二个参数）。
- Arrays 类中含有一个名为 stream() 的静态方法用于把数组转换成为流。
- 等等。。。

### 2. 中间操作

- peek() 操作的目的是帮助调试。它允许你无修改地查看流中的元素。返回由该流的元素组成的流，并在从结果流中消耗元素时对每个元素执行提供的操作。
- sorted() 排序操作
- distinct() 消除重复元素
- filter(Predicate) 过滤操作，结果为true，保留元素
- map(Function<? super T, ? extends R> mapper) 应用函数到元素，mapToInt,mapToLong,mapToDouble 同理
- flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) 将产生流的函数应用在每个元素上（与 map() 所做的相同），然后将每个流都扁平化为元素，因而最终产生的仅仅是元素。

### 3. 终端操作

- toArray()/toArray(generator) 将流转化成适当类型的数组。
- forEach(Consumer)/forEachOrdered(Consumer) 第一种形式，无序操作。第二种形式，保证forEach按照原始流顺序操作。
- collect(Collector) 使用Collector收集元素到集合中
- collect(Supplier, accumulator, combiner) 
- reduce(BinaryOperator) 组合所有流中的元素
- reduce(identity, BinaryOperator) 带初始值，组合所有流中的元素
- reduce(identity, BiFunction, BinaryOperator) 
- allMatch(Predicate) 如果流的每个元素提供给 Predicate 都返回 true ，结果返回为 true。在第一个 false 时，则停止执行计算。
- anyMatch(Predicate) 如果流的任意一个元素提供给 Predicate 返回 true ，结果返回为 true。在第一个 true 是停止执行计算。
- noneMatch(Predicate) 如果流的每个元素提供给 Predicate 都返回 false 时，结果返回为 true。在第一个 true 时停止执行计算。
- findFirst() 返回第一个流元素的 Optional，如果流为空返回 Optional.empty。
- findAny() 返回含有任意流元素的 Optional，如果流为空返回 Optional.empty。
- count() 元素个数
- max() 根据所传入的 Comparator 所决定的“最大”元素。
- min() 根据所传入的 Comparator 所决定的“最小”元素。

## Optional 类

Streams 流操作的终端操作，必须考虑在一个空Stream中获取元素会发生什么。是否有存在某个对象，可用持有流元素的同时，即使查找元素不存在时，也能友好地提示。（不发生异常）

Optional 可以实现，当调用 findFirst()方法，返回一个包含第一个元素的Optional对象，如果流为空，则返回Optional.empty,
当调用 findAny()方法，返回一个包含任意元素的Optional对象，如果流为空，则返回Optional.empty, 若果调用max()/min()方法, 返回一个包含最大值/最小值的Optional对象， 如果流为空，则返回Optional.empty.

当流为空的时候, 你会获得一个 Optional.empty 对象，而不是抛出异常。Optional 拥有 toString() 方法可以用于展示有用信息。

当你接收到 Optional 对象时，应首先调用 isPresent() 检查其中是否包含元素。如果存在，可使用 get() 获取。

几个便利函数：

- ifPresent(Consumer) 如果存在值，则使用该值调用指定的使用者Consumer函数，否则不执行任何操作。
- orElse(otherObject) 如果存在则返回值，否则返回otherObject 。
- orElseGet(Supplier) 如果存在则返回该值，否则调用supplier函数并返回该supplier函数的结果。
- orElseThrow(Supplier) 如果存在则返回该值，否则调用supplier函数并返回该supplier函数的异常。

### 创建Optional

- empty() 生成一个空Optional
- of(value) 生成一个非空Optional
- ofNullable(value) 生成一个可能为空Optional，value为空则生成Optional.empty

### Optional对象操作

- filter(Predicate) 如果存在一个值，并且该值与给定的谓词匹配，则返回一个描述该值的Optional ，否则返回一个空的Optional 。
- map(Function) 如果存在值，则对其应用提供的映射函数，如果结果为非 null，则返回描述结果的Optional 。否则返回一个空的Optional 。
- flatMap(Function<? super T, Optional<U>> mapper)