# EasyChart
A light chart view for Android, including Line Chart and Histogram, have fun and write your own chart!

## ScreenShots
### Ordinary
![withclip](https://github.com/huzenan/EasyChart/blob/master/screenshots/with%20clip.gif) 
### Close canvas clip, we can see that graph only be drawn within the limits of what we set, this speed up the drawing.
![withoutclip](https://github.com/huzenan/EasyChart/blob/master/screenshots/without%20clip.gif)
### First Quartile Mode & Animation
![firstquartile](https://github.com/huzenan/EasyChart/blob/master/screenshots/first%20quartile%20anim.gif)

## Usage
>layout

```xml
    <com.hzn.library.EasyCoordinate
        android:id="@+id/chart"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@+id/layout_select"
        android:padding="20dp"
        custom:ecAxisColor="@color/colorPrimary"
        custom:ecAxisWidth="2dp"
        custom:ecDrawArrow="true"
        custom:ecFirstQuartileMode="false"
        custom:ecGridColor="#f9d180"
        custom:ecGridShow="true"
        custom:ecGridUnitX="50"
        custom:ecGridUnitY="50"
        custom:ecGridWidth="1dp"
        custom:ecMaxFactorX="3.0"
        custom:ecMaxFactorY="3.0"
        custom:ecMinFactorX="0.5"
        custom:ecMinFactorY="0.5"
        custom:ecMoveMode="XY"
        custom:ecScaleMode="XY"
        custom:ecOriginalXPercent="0.25"
        custom:ecOriginalYPercent="0.15"
        />
```
>Activity

```java
    // Point List
    ArrayList<EasyPoint> pointList1 = new ArrayList<>();
    pointList1.add(new EasyPoint());
    ...
    
    // Graph, you can write any graph you like
    EasyGraphLine graph1 = new EasyGraphLine(
                this.getResources().getColor(R.color.colorPoint),
                this.getResources().getColor(R.color.colorPointSelected),
                this.getResources().getColor(R.color.colorSelectedBg),
                dipToPx(2.0f),
                dipToPx(5.0f),
                this.getResources().getColor(R.color.colorPath),
                dipToPx(2.0f));
    
    // Listener
    graph1.setOnPointSelectedListener(new EasyGraphLine.OnPointSelectedListener() {
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
    easyCoordinate.setData("graph_name_1", new EasyCoordinate.EasyCoordinateEntity(pointList1, graph1));
    easyCoordinate.setData("graph_name_2", new EasyCoordinate.EasyCoordinateEntity(pointList2, graph2));
    
    // Animation
    easyCoordinate.initAnimation("graph_name_1", true); // true if enable animation
    easyCoordinate.startAnimation("graph_name_1");
    easyCoordinate.initAnimation("graph_name_2", false);
    easyCoordinate.startAnimation("graph_name_2");
    
    // Remove or Clear Data
    easyCoordinate.removeData("graph_name_1"); // remove graph also
    easyCoordinate.clearData("graph_name_1"); // only clear pointList
```
