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

import org.wso2.esb.integration.services.jaxrs.musicsample.bean.Music;
import org.wso2.esb.integration.services.jaxrs.musicsample.bean.Singer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/music")
public class MusicRestService {

    @Inject
    private MusicService musicService;

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Music getMusicInJSON(@QueryParam("album") final String albumName) {
        /*            Music music = new Music();
       music.setAlbum("Beat It !!!");
       music.setSinger("Micheal Jackson");*/

        return musicService.getByAlbum(albumName);
        //return musicService.musicCollection.get("Dimuthu");

    }

    @POST
    @Path("/post")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMusicInJSONPOST(Music music /*@PathParam("album") String album, @PathParam("singer") String singer*/) {

        musicService.setMusic(music);

        String result = "Album Added in POST : " + music;
        return Response.status(201).entity(result).build();
        //return music.getAlbum();

    }

    @POST
    @Path("/postjson")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMusicInJSONResponsePOST(Music music /*@PathParam("album") String album, @PathParam("singer") String singer*/) {

        musicService.setMusic(music);

        //String result = "Album Added in POST : " + music;
        return Response.status(201).entity(music).build();
        //return music.getAlbum();

    }

    @PUT
    @Path("/put")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMusicInJSONPUT(Music music) {

        Music musicOne = musicService.getByAlbum(music.getAlbum());

        musicOne.setSinger(music.getSinger());

        String result = "Album updated form PUT request: " + musicOne;
        return Response.status(201).entity(result).build();
        //return music;

    }

    @POST
    @Path("/add_singer_details")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSingerDetailsInJSONPOST(Singer singer) {

        musicService.setSinger(singer);

        String result = "Singer Added in POST : " + singer;
        return Response.status(201).entity(result).build();
        //return music.getAlbum();

    }

    @GET
    @Path("/get_singer_details")
    @Produces(MediaType.APPLICATION_JSON)
    public Singer getSingerDetailsInJSON(@QueryParam("singer") final String singerName) {
        /*            Music music = new Music();
       music.setAlbum("Beat It !!!");
       music.setSinger("Micheal Jackson");*/

        return musicService.getBySinger(singerName);
        //return musicService.musicCollection.get("Dimuthu");

    }


}