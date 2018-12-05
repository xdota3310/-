
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

/**
     * @param hash key的hash值
     * @param key 键
     * @param value 值
     * @param onlyIfAbsent 设为true表示如果键不存在，才会写入值。
     * @param evict 
     * @return 返回value
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i; // 定义元素数组、当前元素变量
        // 如果当前Map的元素数组为空 或者 数组长度为0，那么需要初始化元素数组
        // tab = resize() 初始化了元素数组，resize方法同时也可以实现数组扩容，可参见：resize方法解析
        if ((tab = table) == null || (n = tab.length) == 0) 	
            n = (tab = resize()).length; // n 设置为 数组长度
 
        // 根据hash值和数组长度取摸计算出数组下标
        if ((p = tab[i = (n - 1) & hash]) == null)  // 如果该位置不存在元素，那么创建一个新元素存储到数组的该位置。
            tab[i] = newNode(hash, key, value, null); // 此处单独解析
        else { // 如果该位置已经存在元素，说明有以下情况
            Node<K,V> e; K k; // e 用来指向根据key匹配到的元素
            // 如果要写入的key的hash值和当前元素的key的hash值相同，并且key也相等
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p; // 用e指向到当前元素
 
            // 如果要写入的key的hash值和当前元素的key的hash值不同，或者key不相等，说明不是同一个key，要通过其他数据结构来存储新来的数据
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value); // 参见：putTreeVal方法解析
            else { // 运行到这里，说明采用链表结构来存储
                for (int binCount = 0; ; ++binCount) {  // 要逐一对比看要写入的key是否和链表上的某个key相同
                    if ((e = p.next) == null) { // 如果当前元素没有下一个节点
                        // 根据键值对创建一个新节点，挂到链表的尾部
                        p.next = newNode(hash, key, value, null);
                        //  如果链表上元素的个数已经达到了阀值（可以改变存储结构的临界值），
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            // 将该链表上所有元素改为TreeNode方式存储（是为了增加查询性能，元素越多，链表的查询性能越差） 或者 扩容
                            treeifyBin(tab, hash); // 参见：treeifyBin方法解析
                        break;// 跳出循环，因为没有可遍历的元素了
                    }
                    // 如果下一个节点的 hash值和key值都和要写入的hash 和 key相同
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;	// 跳出循环，因为找到了相同的key对应的元素
                    p = e;
                }
            }
            if (e != null) { // 说明找了和要写入的key对应的元素，根据情况来决定是否覆盖值
                V oldValue = e.value; // 旧值
                if (!onlyIfAbsent || oldValue == null)	// 如果旧值为空  后者  指定了需要覆盖旧值，那么更改元素的值为新值
                    e.value = value;
                afterNodeAccess(e); // 元素被访问之后的后置处理， LinkedHashMap中有具体实现
                return oldValue; // 返回旧值
            }
        }
 
        // 执行到这里，说明是增加了新的元素，而不是替换了老的元素，所以相关计数需要累加
 
        ++modCount; // 修改计数器递增
        // 当前map的元素个数递增
        if (++size > threshold) // 如果当前map的元素个数大于了扩容阀值，那么需要扩容元素数组了
            resize(); // 元素数组扩容
        afterNodeInsertion(evict); // 添加新元素之后的后后置处理， LinkedHashMap中有具体实现
        return null; // 返回空
    }
resize方法解析
        if ((tab = table) == null || (n = tab.length) == 0) 	
            n = (tab = resize()).length; // n 设置为 数组长度

        // 根据hash值和数组长度取摸计算出数组下标
        if ((p = tab[i = (n - 1) & hash]) == null)  // 如果该位置不存在元素，那么创建一个新元素存储到数组的该位置。
            tab[i] = newNode(hash, key, value, null); // 此处单独解析
        else { // 如果该位置已经存在元素，说明有以下情况
            Node<K,V> e; K k; // e 用来指向根据key匹配到的元素
            // 如果要写入的key的hash值和当前元素的key的hash值相同，并且key也相等
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p; // 用e指向到当前元素

            // 如果要写入的key的hash值和当前元素的key的hash值不同，或者key不相等，说明不是同一个key，要通过其他数据结构来存储新来的数据
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value); // 参见：putTreeVal方法解析
            else { // 运行到这里，说明采用链表结构来存储
                for (int binCount = 0; ; ++binCount) {  // 要逐一对比看要写入的key是否和链表上的某个key相同
                    if ((e = p.next) == null) { // 如果当前元素没有下一个节点
                        // 根据键值对创建一个新节点，挂到链表的尾部
                        p.next = newNode(hash, key, value, null);
                        //  如果链表上元素的个数已经达到了阀值（可以改变存储结构的临界值），
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            // 将该链表上所有元素改为TreeNode方式存储（是为了增加查询性能，元素越多，链表的查询性能越差） 或者 扩容
                            treeifyBin(tab, hash); // 参见：treeifyBin方法解析
                        break;// 跳出循环，因为没有可遍历的元素了
                    }
                    // 如果下一个节点的 hash值和key值都和要写入的hash 和 key相同
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;	// 跳出循环，因为找到了相同的key对应的元素
                    p = e;
                }
            }
            if (e != null) { // 说明找了和要写入的key对应的元素，根据情况来决定是否覆盖值
                V oldValue = e.value; // 旧值
                if (!onlyIfAbsent || oldValue == null)	// 如果旧值为空  后者  指定了需要覆盖旧值，那么更改元素的值为新值
                    e.value = value;
                afterNodeAccess(e); // 元素被访问之后的后置处理， LinkedHashMap中有具体实现
                return oldValue; // 返回旧值
            }
        }

        // 执行到这里，说明是增加了新的元素，而不是替换了老的元素，所以相关计数需要累加

        ++modCount; // 修改计数器递增
        // 当前map的元素个数递增
        if (++size > threshold) // 如果当前map的元素个数大于了扩容阀值，那么需要扩容元素数组了
            resize(); // 元素数组扩容
        afterNodeInsertion(evict); // 添加新元素之后的后后置处理， LinkedHashMap中有具体实现
        return null; // 返回空
    }