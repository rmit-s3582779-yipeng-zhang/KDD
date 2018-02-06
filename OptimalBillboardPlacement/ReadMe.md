## Explanation

a. **billboard.txt** -> **billboard.rtree** -> **billboardProcessedResult.txt** -> **billboradFinalResult.txt** -> **billboardCombineResult.txt**

1. **billboard.txt** file data format <**panelID~weeklyImpression, longitude, latitude**>.

2. **billboardProcessedResult.txt** file data format :

   < panelID~weeklyImpression, longitude, latitude

   ​	googleRouteCoorID~googleRouteID, longitude, latitude

   ​	... ...

   ​	googleRouteCoorID~googleRouteID, longitude, latitude>

3. **billboardFinalResult.txt** file data format :

   < **panelID~weeklyImpression  googleRouteID,  ... , googleRouteID**>

4. **billboardCombineResult.txt** combine same influence billboards.