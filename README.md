##RecyclerView的对象池RecyclerViewPool

前面提到了，RecyclerView的优势是多个RecyclerView可以共用一个对象池，显而易见，这样能减少View的创建，提高性能，这种场景特别适合于像应用市场或资讯类使用ViewPage+ListView(RecyclerView)的场合。

这里说的对象池就是RecyclerViewPool。在使用上也非常简单，维护一个实例，然后调用recyclerView.setRecycledViewPool(myPool);但是对于LinearLayoutManager(GridLayoutManager)还需要一点配合，需要设置setRecycleChildrenOnDetach(true)。

以下代码是一个Fragment的写法，供参考。




```
public class PageFragment extends Fragment {
 

    public static PageFragment newInstance() {
        PageFragment fragment = new PageFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setRecycleChildrenOnDetach(true);
        recyclerView.setLayoutManager(manager);
        List<PackageInfo> packageInfos = view.getContext().getPackageManager().getInstalledPackages(0);
        recyclerView.setAdapter(new MyAdapter(packageInfos));
        recyclerView.setRecycledViewPool(myPool);
        return view;
    }

    static RecyclerView.RecycledViewPool myPool = new RecyclerView.RecycledViewPool();
}
```

而且，你还可以指定缓存的大小，通过调用myPool.setMaxRecycledViews(0, 10);来设置每种类型的View的容量。

需要注意的是对于ViewType的定义，对同一个RecycerViewPool要统一，因为在RecyclerView的实现中，是以ViewType来作Key来获取对应的对象池。否则就会出现奇怪的问题。

```
 public ViewHolder getRecycledView(int viewType) {
            final ArrayList<ViewHolder> scrapHeap = mScrap.get(viewType);
            if (scrapHeap != null && !scrapHeap.isEmpty()) {
                final int index = scrapHeap.size() - 1;
                final ViewHolder scrap = scrapHeap.get(index);
                scrapHeap.remove(index);
                return scrap;
            }
            return null;
        }
```


##原生的滑动事件与拖拽交换功能

在研究RecyclerView的源码时，发现了ItemTouchHelper类，没想到它竟然能很方便的实现滑动事件与拖拽交换功能。

ItemTouchHelper的构造函数很简单，传一个CallBack即可。而Android中也内置了一个SimpleCallBack的抽象类，帮我们实现了一些方法，我们只要关注重点操作即可。

先上代码，再解释。

```
 ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(packageInfos, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(),  target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                packageInfos.remove(pos);
                adapter.notifyItemRemoved(pos);

            }
        });
        helper.attachToRecyclerView(recyclerView);
```

首先介绍下SimpleCallback的构造方法

```
SimpleCallback(int dragDirs, int swipeDirs)
```
第一个参数dragDirs，是定义可以拖拽的方向，如果对于LinearLayoutManager只需要在上下两个方向进行，这时可以传ItemTouchHelper.UP | ItemTouchHelper.DOWN，如果是对于GridLayoutManager，那么四个方向都会需要，可以传ItemTouchHelper.UP | ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT。

第二个参数是swipeDirs, 这是定义滑动的方向，在操作习惯上，一般都是左右滑动时进行删除，则ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT就可以了。

SimpleCallback是个抽象类，我们需要实现两个方法，

```
public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){}
public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction){}
```
对于onMove方法
第一个方法是在交换两个Item位置时用到，第二个参数是当前被拖拽的Item，第三个参数是把第二个拖到的位置，其返回值是用来判断是否能够交换。

我的实现非常简单，第一步将数据列表根据这两个item的位置对数据本身进行交换。位置可以使用viewHolder.getAdapterPosition()来获取。

```
Collections.swap(packageInfos, viewHolder.getAdapterPosition(), target.getAdapterPosition());
```
第二步通知RecyclerView进行数据更新。
            

```
    adapter.notifyItemMoved(viewHolder.getAdapterPosition(),  target.getAdapterPosition());
```

对于onSwiped方法，我这里做的是删除操作。第一参数被滑动的Item，第二个参数是滑动的方向。
我在示例中做了两步，第一步将数据从列表中删除

```
int pos = viewHolder.getAdapterPosition();
                packageInfos.remove(pos);
```
第二步，通知更新

            

```
    adapter.notifyItemRemoved(pos);
```

代码下载：https://github.com/mutsinghua/RecyclerViewAdvancedDemo