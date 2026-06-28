package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPet {

    private static final String BASE_URL = "http://5.181.109.28:9090/api/v3";

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("sergey zhitlov")
    public void testDeleteNonexistentPet() {
        Response response = step("Отправить DELETE запрос на удаление несуществующего Pet", () ->
                given()
                    .contentType(ContentType.JSON)
                    .header("Accept", "application/json")
                        .when()
                    .delete(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус-код ответа == 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
                );

        step("Проверить, что текст ответа 'Pet deleted'", () ->
                assertEquals("Pet deleted", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody)
                );
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("sergey zhitlov")
    public void testUpdeteNonexistentPet() {
        Pet pet = new Pet();
        pet.setId(9999);
        pet.setName("Non-existen Pet");
        pet.setStatus("available");

        Response response = step("Отправить PUT запрос на обновление несуществующего питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус-код ответа == 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверить, что текст ответа 'Pet not found'", () ->
                assertEquals("Pet not found", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody)
        );


    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("sergey zhitlov")
    public void testGettingInformationNonexistentPet() {
        Response response = step("Отправить GET запрос на ролучение информации о несуществующем питомце", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .get(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус-код ответа == 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверить, что текст ответа 'Pet not found'", () ->
                assertEquals("Pet not found", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody)
        );
    }

    @ParameterizedTest (name = "добавление питомца со статусом: {2}")
    @CsvSource({
            "378, Bobik, available",
            "379, Tuzik, pending",
            "380, Sharik, sold"
    })
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("sergey zhitlov")
    public void testAddNewPet(int id, String name, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);

        Response response = step("Отправить POST запрос на создание нового питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить что статус код ответ == 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверка параметров созданного питомца", () -> {
                    Pet createdPet = response.as(Pet.class);
                    assertEquals(pet.getId(),createdPet.getId(), "id питомца не совпадает с ожидаемым");
                    assertEquals(pet.getName(),createdPet.getName(), "имя питомца не совпадает с ожидаемым");
                    assertEquals(pet.getStatus(),createdPet.getStatus(), "статус питомца не совпадает с ожидаемым");
                }
        );
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("sergey zhitlov")
    public void testAddNewPetWithNonExistentStatus() {
        Pet pet = new Pet();
        pet.setId(400);
        pet.setName("Murzik");
        pet.setStatus("cat");

        Response response = step("Отправить POST запрос на создание нового питомца с несуществующим статусом", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить что статус код ответ == 400", () ->
                assertEquals(400, response.getStatusCode(),
                    "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверить, что текст ответа 'Invalid pet status. Valid values: [available, pending, sold]'", () ->
                assertEquals("Invalid pet status. Valid values: [available, pending, sold]", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody)
        );
    }
}
