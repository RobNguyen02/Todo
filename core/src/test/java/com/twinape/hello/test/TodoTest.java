package com.twinape.hello.test;

import com.twinape.hello.api.todo.*;
import com.twinape.hello.repo.Todo.Todo;
import com.twinape.hello.repo.Todo.TodoRepo;
import com.twinape.facade.IRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TodoTest {

    private static TodoRepo todoRepo;
    private static CreateTodoApi createTodoApi;
    private static UpdateTodoApi updateTodoApi;
    private static DeleteTodoApi deleteTodoApi;
    private static GetTodoApi getTodoApi;
    private static GetTodoByIdApi getTodoByIdApi;


    @BeforeAll
    static void setup() {
        todoRepo = mock(TodoRepo.class);
        createTodoApi = new CreateTodoApi(todoRepo);
        updateTodoApi = new UpdateTodoApi(todoRepo);
        deleteTodoApi = new DeleteTodoApi(todoRepo);
        getTodoApi = new GetTodoApi(todoRepo);
        getTodoByIdApi = new GetTodoByIdApi(todoRepo);
    }


    /*    class  FakeUpdateTodoRequest {
                public Integer id = 1;
                public String title = "Test Title update";
                public String descr = "Update test description";
                public boolean is_complete = true;
                } */


    //Fake data list
    List<Todo> faketodo = List.of(
            Todo.builder()
                    .id(1)
                    .title("Fake title 1")
                    .descr("Fake descr 1")
                    .complete(false)
                    .build(),
            Todo.builder()
                    .id(2)
                    .title("Fake title 2")
                    .descr("Fake descr 2")
                    .complete(false)
                    .build(),
            Todo.builder()
                    .id(3)
                    .title("Fake title 3")
                    .descr("Fake descr 3")
                    .complete(false)
                    .build());


    @Test
    void testHandle_UpdateSuccessMessage() throws Exception {
        int id = 1;
        String title = "Test Title update";
        String descr = "Test Descr update";
        boolean isComplete = true;

        //fake request body
        UpdateTodoApi.UpdateTodoRequest requestBody = UpdateTodoApi.UpdateTodoRequest.builder()
                .id(id)
                .title(title)
                .descr(descr)
                .is_complete(isComplete)
                .build();

        //Mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(UpdateTodoApi.UpdateTodoRequest.class)).thenReturn(requestBody);


        //Mock repo behavior
        when(todoRepo.updateTodo(id, title, descr, isComplete))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        var resultFuture = updateTodoApi.handle(request);
        var result = resultFuture.toCompletableFuture().get(); // unwrap future

        // Assert
        assertTrue(result instanceof Map);
        Map<?, ?> resultMap = (Map<?, ?>) result;
        assertEquals("Update todo with id: " + id, resultMap.get("message"));

        // Verify repo call
        verify(todoRepo).updateTodo(id, title, descr, isComplete);

    }

    @Test
    void testHandle_CreateReturnSuccessMessage() throws Exception {
        // Arrange
        String title = "Test Title create";
        String descr = "Some description create";
        boolean isComplete = true;

        // Fake request body
        CreateTodoApi.CreateTodoRequest requestBody = CreateTodoApi.CreateTodoRequest.builder()
                .title(title)
                .descr(descr)
                .is_complete(isComplete)
                .build();

        // Mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(CreateTodoApi.CreateTodoRequest.class)).thenReturn(requestBody);

        // Mock repo behavior
        when(todoRepo.createtodo(title, descr, isComplete))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        var resultFuture = createTodoApi.handle(request);
        var result = resultFuture.toCompletableFuture().get(); // unwrap future

        // Assert
        assertTrue(result instanceof Map);
        Map<?, ?> resultMap = (Map<?, ?>) result;
        assertEquals("Create success todo with title: " + title, resultMap.get("message"));

        // Verify repo call
        verify(todoRepo).createtodo(title, descr, isComplete);
    }

    @Test
    void testHandle_DeleteReturnSuccessMessage() throws Exception {
        int id = 1;

        //fake request body
        DeleteTodoApi.DeleteTodoRequest requestBody = DeleteTodoApi.DeleteTodoRequest.builder()
                .id(id)
                .build();

        //Mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(DeleteTodoApi.DeleteTodoRequest.class)).thenReturn(requestBody);

        //Mock repo
        when(todoRepo.deleteTodo(id)).thenReturn(CompletableFuture.completedFuture(null));

        var resultFuture = deleteTodoApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //Asert
        assertTrue(result instanceof Map);
        Map<?, ?> resultMap = (Map<?, ?>) result;
        assertEquals("Deleted todo with ID: " + id, resultMap.get("message"));


        verify(todoRepo).deleteTodo(id);

    }

    @Test
    void testHandle_GetAll() throws Exception {
        int limit = 10;
        int offset = 0;

        //fake request
        GetTodoApi.GetTodoRequest requestBody = GetTodoApi.GetTodoRequest.builder()
                .limit(limit)
                .offset(offset)
                .build();

        //Mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(GetTodoApi.GetTodoRequest.class)).thenReturn(requestBody);

        //Mock repo
        when(todoRepo.getAllTodo(limit, offset)).thenReturn(CompletableFuture.completedFuture(faketodo));

        //Act
        var resultFuture = getTodoApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //asert
        assertTrue(result instanceof List);
        assertEquals(3, result.size());

        //veryfy repo call
        verify(todoRepo).getAllTodo(limit, offset);
    }

    @Test
    void testHandle_GetOneTodo() throws Exception {
        int id = 1;

        //fake request
        GetTodoByIdApi.GetTodoByIdRequest requestBody = GetTodoByIdApi.GetTodoByIdRequest.builder()
                .id(id)
                .build();

        //mock request
        IRequest request = mock(IRequest.class);
        when(request.getBodyAs(GetTodoByIdApi.GetTodoByIdRequest.class)).thenReturn(requestBody);

        //fake data
        Todo fakeTodoid = Todo.builder()
                .id(id)
                .title("Fake title by id")
                .descr("Fake descr by id")
                .complete(true)
                .build();

        //mock repo
        when(todoRepo.getTodo(id)).thenReturn(CompletableFuture.completedFuture(fakeTodoid));

        //Act
        var resultFuture = getTodoByIdApi.handle(request);
        var result = resultFuture.toCompletableFuture().get();

        //Assert
        assert (result instanceof Todo);
        assertEquals(fakeTodoid, result);

        //repo verify
        verify(todoRepo).getTodo(id);
    }

}
