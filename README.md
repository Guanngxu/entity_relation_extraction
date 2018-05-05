# 基于依存句法分析的实体关系抽取

### 功能

使用依存句法分析抽取非结构化数据中的事实三元组（实体，关系，实体）

### 使用

* 项目需要安装HanLP，相关说明请参见[HanLP使用文档](http://hanlp.linrunsoft.com/doc.html)
* 然后运行main/Main.class即可
* 建议导入用户词典

### Dependency

HanLP依存句法分析

### 参考

[基于依存分析的开放式中文实体关系抽取方法](http://www.docin.com/p-1715877509.html)

命名实体三元组抽取参考自[fact_triple_extraction](https://github.com/twjiang/fact_triple_extraction)

### 存在的bug

bug.md中记录了原来发现的一个bug，里面有详细的说明。

但是经过测试，发现在一些情况下，completeEntity这个函数仍然会导致程序出错，也没有再进行调试了。

可以选择性的将使用到completeEntity函数的地方注释掉。
