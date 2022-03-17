# 并发编程

## 术语问题

合理区分：

- 并发是关于正确有效地控制对共享资源的访问。
- 并行是使用额外的资源来更快地产生结果。

> “并发”通常表示：”不止一个任务正在执行“。而“并行”几乎总是代表：”不止一个任务同时执行“。现在我们能立即看出这些定义中的问题所在：“并行”也有不止一个任务正在执行的语义在里面。区别就在于细节：究竟是怎么“执行”的。此外还有一些重叠：为并行编写的程序依旧可以在单处理器上运行，而并发编写的系统也可以利用多个处理器。
