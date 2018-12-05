/**
* 获取key对应的值，如果找不到则返回null
* 但是如果返回null并不意味着就没有找到，也可能key对应的值就是null，因为HashMap允许null值（也允许null键）
* 在返回值为null时，可以通过containsKey来方法来区分到底是因为key不存在，还是key对应的值就位null
*/
public V get(Object key) {
    Node<K,V> e; // 声明一个节点对象（键值对对象）
    // 调用getNode方法来获取键值对，如果没有找到返回null，找到了就返回键值对的值
    return (e = getNode(hash(key), key)) == null ? null : e.value; //真正的查找过程都是通过getNode方法实现的
}
 
/**
* 检查是否包含key
* 如果key有对应的节点对象，则返回ture，不关心节点对象的值是否为空
*/
public boolean containsKey(Object key) {
    // 调用getNode方法来获取键值对，如果没有找到返回false，找到了就返回ture
    return getNode(hash(key), key) != null; //真正的查找过程都是通过getNode方法实现的
}
 
/**
* 该方法是Map.get方法的具体实现
* 接收两个参数
* @param hash key的hash值，根据hash值在节点数组中寻址，该hash值是通过hash(key)得到的，可参见：hash方法解析
* @param key key对象，当存在hash碰撞时，要逐个比对是否相等
* @return 查找到则返回键值对节点对象，否则返回null
*/
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k; // 声明节点数组对象、链表的第一个节点对象、循环遍历时的当前节点对象、数组长度、节点的键对象
    // 节点数组赋值、数组长度赋值、通过位运算得到求模结果确定链表的首节点
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // 首先比对首节点，如果首节点的hash值和key的hash值相同 并且 首节点的键对象和key相同（地址相同或equals相等），则返回该节点
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first; // 返回首节点
 
        // 如果首节点比对不相同、那么看看是否存在下一个节点，如果存在的话，可以继续比对，如果不存在就意味着key没有匹配的键值对    
        if ((e = first.next) != null) {
            // 如果存在下一个节点 e，那么先看看这个首节点是否是个树节点
            if (first instanceof TreeNode)
                // 如果是首节点是树节点，那么遍历树来查找
                return ((TreeNode<K,V>)first).getTreeNode(hash, key); 
 
            // 如果首节点不是树节点，就说明还是个普通的链表，那么逐个遍历比对即可    
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k)))) // 比对时还是先看hash值是否相同、再看地址或equals
                    return e; // 如果当前节点e的键对象和key相同，那么返回e
            } while ((e = e.next) != null); // 看看是否还有下一个节点，如果有，继续下一轮比对，否则跳出循环
        }
    }
    return null; // 在比对完了应该比对的树节点 或者全部的链表节点 都没能匹配到key，那么就返回null
}
