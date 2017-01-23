/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.esb.integration.services.jaxrs.musicsample;

import org.springframework.stereotype.Service;
import org.wso2.esb.integration.services.jaxrs.musicsample.bean.Music;
import org.wso2.esb.integration.services.jaxrs.musicsample.bean.Singer;

import java.util.concurrent.ConcurrentHashMap;


@Service
public class MusicService {

    private final ConcurrentHashMap< String, Music> musicCollection = new ConcurrentHashMap< String, Music>();
    private final ConcurrentHashMap< String,Singer> singerCollection = new ConcurrentHashMap< String, Singer >();


    public MusicService() {
        init();
    }

    final void init() {
        System.out.println("Welcome To The World Of Music .... ");

        Music music = new Music();
        music.setAlbum("Gold");
        music.setSinger("Elton John");
        musicCollection.put(music.getAlbum(), music);

        Music music2 = new Music();
        music2.setAlbum("THE ENDLESS RIVER");
        music2.setSinger("UB40");
        musicCollection.put(music2.getAlbum(), music2);

        Singer singer = new Singer();
        singer.setName("Eric Clapton");
        singer.setAge(45);
        singerCollection.put(singer.getName(),singer);
    }

    public Music getByAlbum( final String albumName ) {

        return musicCollection.get(albumName);
    }

    public void setMusic(Music music ) {

        musicCollection.put(music.getAlbum(),music);
    }


    public Singer getBySinger( final String singer ) {

        return singerCollection.get(singer);
    }

    public void setSinger(Singer singer ) {

        singerCollection.put(singer.getName(),singer);
    }
}
