# EasyChart
A light chart view for Android, chart type can be EXTENDED very easily.

## ScreenShots
### Ordinary
![withclip](https://github.com/huzenan/EasyChart/blob/master/screenshots/with%20clip.gif) 
### Close canvas clip, we can see that graph only be drawn within the limits of what we set, this speed up the drawing.
![withoutclip](https://github.com/huzenan/EasyChart/blob/master/screenshots/without%20clip.gif)

## Usage
>layout

```xml
    <com.hzn.library.EasyCoordinate
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="20dp"
        custom:ecAxisColor="#3cb998"
        custom:ecAxisWidth="2dp"
        custom:ecDrawArrow="true"
        custom:ecGridColor="#f9d180"
        custom:ecGridUnitX="50"
        custom:ecGridUnitY="50"
        custom:ecGridWidth="1dp"
        custom:ecMaxFactorX="3.0"
        custom:ecMaxFactorY="3.0"
        custom:ecMinFactorX="0.5"
        custom:ecMinFactorY="0.5"
        custom:ecOriginalXPercent="0.25"
        custom:ecOriginalYPercent="0.15"
        custom:ecBackgroundColor="#ffffff"
        />
```
>Activity

```java
    // Point List
    ArrayList<EasyPoint> pointList = new ArrayList<>();
    pointList.add(new EasyPoint());
    ...
    
    // Graph, you can write any graph you like
    EasyGraphLine graph = new EasyGraphLine(
                this.getResources().getColor(R.color.colorPoint),
                this.getResources().getColor(R.color.colorPointSelected),
                this.getResources().getColor(R.color.colorSelectedBg),
                dipToPx(2.0f),
                dipToPx(5.0f),
                this.getResources().getColor(R.color.colorPath),
                dipToPx(2.0f));
    
    // Listener
    graph.setOnPointSelectedListener(new EasyGraphLine.OnPointSelectedListener() {
            @Override
            public void onPointSelected(EasyPoint selectedPoint) {
                // your codes.
            }

            @Override
            public void onPointUnselected(EasyPoint unselectedPoint) {
                // your codes.
            }
        });
        
    // Set Data
    easyCoordinate.setDataList(pointList, graph, true); // true if clear old data
```
