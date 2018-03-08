package com.jyall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
/**
 * HazelCast配置属性
 * @autor: Faizel
 * @date: 2017/7/12
 * @version:1.0.0
 */
@Component
@ConfigurationProperties(prefix="faizel.hazelCast")
public class HazelCastProperties {

    private String port;

    private String multiCast;

    private String members;

    private String mapName;

    private String mapBackUp;

    private String listName;

    private String listBackUp;

    private String listMaxSize;

    public String getPort() {
        return port;
    }

    public int getIntPort(){

        return Integer.parseInt(port);
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMultiCast() {
        return multiCast;
    }

    public boolean getBooleanMultiCast(){

        return "true".equalsIgnoreCase(multiCast);
    }

    public void setMultiCast(String multiCast) {
        this.multiCast = multiCast;
    }

    public String getMembers() {
        return members;
    }

    public List<String> getListMemebers(){

        List<String> list = new ArrayList<String>();

        String[] array = members.split(",");

        for(String tem: array)

            list.add(tem.trim());

        return list;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapBackUp() {
        return mapBackUp;
    }

    public int getIntMapBackUp(){

        return Integer.parseInt(mapBackUp);
    }

    public void setMapBackUp(String mapBackUp) {
        this.mapBackUp = mapBackUp;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListBackUp() {
        return listBackUp;
    }

    public int getIntListBackUp(){

        return Integer.parseInt(listBackUp);
    }

    public void setListBackUp(String listBackUp) {
        this.listBackUp = listBackUp;
    }

    public String getListMaxSize() {
        return listMaxSize;
    }

    public int getIntListMaxSize(){

        return Integer.parseInt(listMaxSize);
    }

    public void setListMaxSize(String listMaxSize) {
        this.listMaxSize = listMaxSize;
    }
}
