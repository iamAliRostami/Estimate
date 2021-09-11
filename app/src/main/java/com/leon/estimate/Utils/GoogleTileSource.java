package com.leon.estimate.Utils;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

public class GoogleTileSource extends OnlineTileSourceBase {
    //Image overlay lyrs=h
    static final String[] baseUrl_GoogleLabel = new String[]{
            "http://mt1.google.cn/vt/imgtp=png32&lyrs=h@210000000&hl=en-US&gl=US&src=app&s=G",
            "http://mt2.google.cn/vt/imgtp=png32&lyrs=h@210000000&hl=en-US&gl=US&src=app&s=G",
            "http://mt3.google.cn/vt/imgtp=png32&lyrs=h@210000000&hl=en-US&gl=US&src=app&s=G"
    };

    //Vector basemap lyrs=m lyrs=refers to the tile type. There is a mark in the country but there is an offset. There is no test abroad
    static final String[] baseUrl_GoogleRoad = new String[]{
            "http://mt1.google.cn/vt/lyrs=m@209712068&hl=en-US&gl=US&src=app&s=G",
            "http://mt2.google.cn/vt/lyrs=m@209712068&hl=en-US&gl=US&src=app&s=G",
            "http://mt3.google.cn/vt/lyrs=m@209712068&hl=en-US&gl=US&src=app&s=G"
    };


    //Image basemap lyrs=y is marked in China but there is offset, no test abroad
    static final String[] baseUrl_Google_cn = new String[]{
            "http://mt0.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G",
            "http://mt1.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G",
            "http://mt2.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G",
            "http://mt3.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G"
    };


    //Image basemap lyrs=s is not marked
    static final String[] baseUrl_GoogleSatellite = new String[]{
            "http://mt0.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G",
            "http://mt1.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G",
            "http://mt2.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G",
            "http://mt3.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G"
    };

    String urlXYZ = "&x={$x}&y={$y}&z={$z}";

    public GoogleTileSource() {
        super("Google", 2, 22, 256, "png", baseUrl_Google_cn);
    }

//    @Override
//    public String getTileURLString(MapTile aTile) {
//        String url = getBaseUrl() + urlXYZ
//                .replace("{$x}", aTile.getX() + "")
//                .replace("{$y}", aTile.getY() + "")
//                .replace("{$z}", aTile.getZoomLevel() + "");
//        return url;
//    }

    @Override
    public String getTileURLString(long pMapTileIndex) {

        return getBaseUrl() + MapTileIndex.getZoom(pMapTileIndex) + "/" + MapTileIndex.getX(pMapTileIndex)
                + "/" + MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding;
    }
}