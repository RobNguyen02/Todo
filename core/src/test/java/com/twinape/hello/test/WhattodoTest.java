package com.twinape.hello.test;

import com.twinape.facade.IRequest;
import com.twinape.hello.api.whattodo.AddWtdApi;

import com.twinape.hello.api.whattodo.DeleteWtdApi;
import com.twinape.hello.api.whattodo.UpdateWtdApi;
import com.twinape.hello.repo.Whattodo.WhattodoRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class WhattodoTest {

    private static WhattodoRepo whattodoRepo;
    private static AddWtdApi addWtdApi;
    private static UpdateWtdApi updateWtdApi;
    private static DeleteWtdApi deleteWtdApi;


    @BeforeAll
    static void setup() {
        whattodoRepo = mock(WhattodoRepo.class);
        addWtdApi = new AddWtdApi(whattodoRepo);
        updateWtdApi = new UpdateWtdApi(whattodoRepo);
        deleteWtdApi = new DeleteWtdApi(whattodoRepo);
    }


    @Test
    void testHandle_AddWtd() throws Exception {
        //Arrange
        String content = "Test wtd add";
        //LocalDateTime starttime = LocalDateTime.parse("2025-05-27T12:00:00");
        LocalDateTime starttime = LocalDateTime.of(2025, 05, 27, 12, 0, 0);
        LocalDateTime endtime = LocalDateTime.of(2025, 05, 27, 12, 0, 0);
        Integer idtodo = 1;

        //fake request body
        AddWtdApi.AddWtdRequest requestBody = AddWtdApi.AddWtdRequest.builder()
                .content(content)
                .starttime(starttime)
                .endtime(endtime)
                .idtodo(idtodo)
                .build();

        //Mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(AddWtdApi.AddWtdRequest.class)).thenReturn(requestBody);

        //mock repo
        when(whattodoRepo.addWhattodo(content, starttime, endtime, idtodo))
                .thenReturn(CompletableFuture.completedFuture(null));

        //act
        var resultFuture = addWtdApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //assert
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("Add success what to do with content: " + content, map.get("message"));

        //verify repo
        verify(whattodoRepo).addWhattodo(content, starttime, endtime, idtodo);
    }


    @Test
    void testHandle_AddWtdContentInvalidData_ReturnError() throws Exception {
        String content = ""; //null
        //LocalDateTime starttime = LocalDateTime.parse("2025-05-27T12:00:00");
        LocalDateTime starttime = LocalDateTime.of(2025, 05, 27, 12, 0, 0);
        LocalDateTime endtime = LocalDateTime.of(2025, 05, 27, 12, 0, 0);
        Integer idtodo = 1;

        //fake requestbody
        AddWtdApi.AddWtdRequest requestBody = AddWtdApi.AddWtdRequest.builder()
                .content(content)
                .starttime(starttime)
                .endtime(endtime)
                .idtodo(idtodo)
                .build();

        //mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(AddWtdApi.AddWtdRequest.class)).thenReturn(requestBody);

        //mock repo
        when(whattodoRepo.addWhattodo(content, starttime, endtime, idtodo))
                .thenReturn(CompletableFuture.completedFuture(null));

        //act
        var resultFuture = addWtdApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //assert
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("Content is required", map.get("error"));

        //verify
        verify(whattodoRepo, never()).addWhattodo(any(), any(), any(), anyInt());
    }


    @Test
    void testHandle_AddWtdEndTimeNotAfterStartTime_ReturnError() throws Exception {
        String content = "Test Content";
        //LocalDateTime starttime = LocalDateTime.parse("2025-05-27T12:00:00");
        LocalDateTime starttime = LocalDateTime.of(2025, 05, 05, 12, 0, 0);
        LocalDateTime endtime = LocalDateTime.of(2024, 04, 04, 12, 0, 0);
        Integer idtodo = 1;

        //fake requestbody
        AddWtdApi.AddWtdRequest requestBody = AddWtdApi.AddWtdRequest.builder()
                .content(content)
                .starttime(starttime)
                .endtime(endtime)
                .idtodo(idtodo)
                .build();

        //mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(AddWtdApi.AddWtdRequest.class)).thenReturn(requestBody);

        //mock repo
        when(whattodoRepo.addWhattodo(content, starttime, endtime, idtodo))
                .thenReturn(CompletableFuture.completedFuture(null));

        //act
        var resultFuture = addWtdApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //assert
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("End time must be after start time", map.get("error"));

        //verify
        verify(whattodoRepo, never()).addWhattodo(any(), any(), any(), anyInt());
    }


    @Test
    void testHandle_UpdateWtd() throws Exception {
        //Arrange
        int id = 1;
        String content = "Test wtd update";
        LocalDateTime starttime = LocalDateTime.of(2025, 05, 05, 05, 05, 0);
        LocalDateTime endtime = LocalDateTime.of(2025, 05, 06, 05, 0, 0);

        //fake request
        UpdateWtdApi.UpdateWtdRequest requestbody = UpdateWtdApi.UpdateWtdRequest.builder()
                .id(id)
                .content(content)
                .starttime(starttime)
                .endtime(endtime)
                .build();

        //mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(UpdateWtdApi.UpdateWtdRequest.class)).thenReturn(requestbody);

        //Mock repo
        when(whattodoRepo.updateWhattodo(id, content, starttime, endtime))
                .thenReturn(CompletableFuture.completedFuture(null));

        //act
        var resultFuture = updateWtdApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //assert
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("Update todo with id: " + id, map.get("message"));

        //verify repo
        verify(whattodoRepo).updateWhattodo(id, content, starttime, endtime);

//        verify(whattodoRepo, never()).deleteWhattodo(anyInt());
//        verify(whattodoRepo, never()).addWhattodo(any(), any(), any(), anyInt());
    }

    @Test
    void testHandle_UpdateWtdContentInvalid_ReturnError() throws Exception {
        Integer id = 1;
        String content = "";
        LocalDateTime starttime = LocalDateTime.of(2025, 05, 05, 05, 05, 0);
        LocalDateTime endtime = LocalDateTime.of(2025, 05, 06, 05, 0, 0);


        //fake requestbody
        UpdateWtdApi.UpdateWtdRequest requestBody= UpdateWtdApi.UpdateWtdRequest.builder()
                .id(id)
                .content(content)
                .starttime(starttime)
                .endtime(endtime)
                .build();

        // mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(UpdateWtdApi.UpdateWtdRequest.class)).thenReturn(requestBody);

        //mock repo
        when(whattodoRepo.updateWhattodo(id,content, starttime,endtime))
                .thenReturn(CompletableFuture.completedFuture(null));

        //act
        var resultFuture = updateWtdApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //assert
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("content is required", map.get("error"));

        //verify
        verify(whattodoRepo,never()).updateWhattodo(id, content, starttime, endtime);

    }


    @Test
    void testHandle_DeleteWtd() throws Exception {
        //arrange
        int id = 1;

        //fake request body
        DeleteWtdApi.DeleteWhattodoRequest requestbody = DeleteWtdApi.DeleteWhattodoRequest.builder()
                .id(id)
                .build();

        //mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(DeleteWtdApi.DeleteWhattodoRequest.class)).thenReturn(requestbody);

        //mock repo
        when(whattodoRepo.deleteWhattodo(id))
                .thenReturn(CompletableFuture.completedFuture(null));

        //act
        var resultFuture = deleteWtdApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //assert
        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("Deleted success whattodo with ID: " + id, map.get("message"));

        //verify repo
        verify(whattodoRepo).deleteWhattodo(id);


    }


}
