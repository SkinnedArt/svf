package gov.pnnl.svf.core.collections;

import gov.pnnl.svf.test.AbstractObjectTestBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Amelia Bleeker
 */
public class AbstractTreeNodeTest extends AbstractObjectTestBase<AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>>> {

    private static final int MAX_CHILDREN = 10;
    private final Random random = new Random();

    public AbstractTreeNodeTest() {
        ignore.put(AbstractTreeNode.class.getName(), Collections.singletonList("parent"));
    }

    @Test
    public void testIterator() {
        final AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> a = newValueObject();
        // print
        final StringBuilder sb = new StringBuilder();
        for (final ListTreeNode<Long> node : a) {
            sb.append(node.getValue().toString());
            sb.append(" ");
        }
        Assert.assertFalse(sb.toString().trim().isEmpty());
    }

    @Test
    public void testIteratorPreOrder() {
        final AtomicLong counter = new AtomicLong();
        final ListTreeNode<Long> a = new ListTreeNode<>(counter.getAndIncrement());
        final List<ListTreeNode<Long>> b = new ArrayList<>();
        final ListTreeNode<Long> c = new ListTreeNode<>(counter.getAndIncrement());
        c.setChildren(Collections.singletonList(new ListTreeNode<>(counter.getAndIncrement())));
        b.add(c);
        final ListTreeNode<Long> d = new ListTreeNode<>(counter.getAndIncrement());
        d.setChildren(Collections.singletonList(new ListTreeNode<>(counter.getAndIncrement())));
        b.add(d);
        final ListTreeNode<Long> e = new ListTreeNode<>(counter.getAndIncrement());
        e.setChildren(Collections.singletonList(new ListTreeNode<>(counter.getAndIncrement())));
        b.add(e);
        a.setChildren(b);
        // print
        final StringBuilder sb = new StringBuilder();
        for (final ListTreeNode<Long> node : a) {
            sb.append(node.getValue().toString());
            sb.append(" ");
        }
        System.out.println(sb.toString());
        Assert.assertEquals("0 1 2 3 4 5 6", sb.toString().trim());
    }

    @Test
    public void testIsRoot() {
        final AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> a = newValueObject();
        Assert.assertTrue(a.isRoot());
        Assert.assertFalse(a.getChildren().get(0).isRoot());
    }

    @Test
    public void testIsLeaf() {
        final AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> a = newValueObject();
        Assert.assertFalse(a.isLeaf());
        Assert.assertFalse(a.getChildren().get(0).isLeaf());
        Assert.assertFalse(a.getChildren().get(0).getChildren().get(0).isLeaf());
        Assert.assertFalse(a.getChildren().get(0).getChildren().get(0).getChildren().get(0).isLeaf());
        Assert.assertTrue(a.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).isLeaf());
    }

    @Test
    public void testGetParent() {
        final AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> a = newValueObject();
        Assert.assertTrue(a.getChildren().get(0).getParent().equals(a));
    }

    @Test
    public void testGetRoot() {
        final AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> a = newValueObject();
        Assert.assertTrue(a.equals(a));

        Assert.assertTrue(a.getRoot().equals(a.getRoot()));
        Assert.assertTrue(a.equals(a.getRoot()));
        Assert.assertTrue(a.getRoot().equals(a));

        Assert.assertFalse(a.equals(a.getChildren().get(0)));
        Assert.assertTrue(a.getRoot().equals(a.getChildren().get(0).getRoot()));
        Assert.assertTrue(a.equals(a.getChildren().get(0).getRoot()));
        Assert.assertFalse(a.getRoot().equals(a.getChildren().get(0)));
    }

    @Override
    protected AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> copyValueObject(final AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> object) {
        return new ListTreeNode<>((ListTreeNode<Long>) object);
    }

    @Override
    protected AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> newValueObject() {
        final ListTreeNode<Long> instance = new ListTreeNode<>(random.nextLong());
        instance.setChildren(createRandom(3));
        return instance;
    }

    @Override
    protected void setFieldsToNull(final AbstractTreeNode<Long, List<ListTreeNode<Long>>, ListTreeNode<Long>> object) {
        // no operation
    }

    private List<ListTreeNode<Long>> createRandom(final int level) {
        if (level < 0) {
            return Collections.emptyList();
        }
        final int count = random.nextInt(MAX_CHILDREN - 1) + 1;
        final List<ListTreeNode<Long>> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final ListTreeNode<Long> node = new ListTreeNode<>(random.nextLong());
            node.setChildren(createRandom(level - 1));
            list.add(node);
        }
        return list;
    }
}
