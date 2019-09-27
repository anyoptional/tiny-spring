
    这个包中是一些用于测试的JavaBeans，下面是对这些类关系的简单介绍。
    
    每个国家(Country)都有一个首都(Capital)。
    首都作为一座城市，自然也对应一个国家，也会有许多的居民(People)。
    People又是由许多独立的个体(Person)组成的，每个人呢都会想拥有自己的一辆车(Car)。
    
    这里面涉及到循环依赖 -- Country --> Capital, Capital --> Country，
    也涉及到数组 -- People --> Person[]，应该足够作为示例来使用。