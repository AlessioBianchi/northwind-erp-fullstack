package com.github.alessiobianchi.erp.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Seed data: 77 products, so with pageSize=50 Chai (id 1, listed last) is only on page 1 without a search.
class SearchIntegrationTest extends AbstractIntegrationTest {

    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        session = login(EMPLOYEE, SEED_PASSWORD);
    }

    @ParameterizedTest
    @ValueSource(strings = {"chai", "CHAI", " Chai "})
    void products_ByName_ShouldFindRowFromAnyPage(String search) throws Exception {
        mockMvc.perform(get("/api/v1/products").session(session)
                        .param("pageNumber", "0").param("pageSize", "50").param("search", search))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].productName").value("Chai"));
    }

    @Test
    void products_ByCategoryName_ShouldMatchCategory() throws Exception {
        mockMvc.perform(get("/api/v1/products?pageNumber=0&pageSize=50&search=beverages").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(12))
                .andExpect(jsonPath("$.content[*].category.categoryName", everyItem(is("Beverages"))));
    }

    @Test
    void products_BySupplierName_ShouldPageResultsNewestFirst() throws Exception {
        mockMvc.perform(get("/api/v1/products?pageNumber=0&pageSize=1&search=exotic").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(2))
                .andExpect(jsonPath("$.content[*].productName", contains("Aniseed Syrup")));

        mockMvc.perform(get("/api/v1/products?pageNumber=1&pageSize=1&search=exotic").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].productName", contains("Chang")));
    }

    @ParameterizedTest
    @CsvSource({
            "customers, alfki,        customerCode, ALFKI",
            "customers, futterkiste,  companyName,  Alfreds Futterkiste",
            "suppliers, exotic,       companyName,  Exotic Liquids",
            "suppliers, charlotte,    contactName,  Charlotte Cooper",
            "employees, nancy,        firstName,    Nancy",
            "employees, davolio,      lastName,     Davolio",
            "employees, sales rep,    title,        Sales Representative"
    })
    void search_ShouldMatchEachSearchedField(String resource, String search, String field, String expected) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource).session(session)
                        .param("pageNumber", "0").param("pageSize", "50").param("search", search))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*]." + field, hasItem(expected)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "products", "suppliers"})
    void blankSearch_ShouldReturnSameTotalAsNoSearch(String resource) throws Exception {
        int total = totalElements("/api/v1/" + resource + "?pageNumber=0&pageSize=50");

        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=0&pageSize=50&search=").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(total));
        mockMvc.perform(get("/api/v1/" + resource).session(session)
                        .param("pageNumber", "0").param("pageSize", "50").param("search", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(total));
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "products", "suppliers"})
    void noMatch_ShouldReturnEmptyPage(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=0&pageSize=50&search=zzzz-no-match").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"customers", "employees", "products", "suppliers"})
    void searchWithInvalidPageSize_ShouldStillReturn400(String resource) throws Exception {
        mockMvc.perform(get("/api/v1/" + resource + "?pageNumber=0&pageSize=101&search=a").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("pageSize: must be at most 100"));
    }

    private int totalElements(String url) throws Exception {
        String body = mockMvc.perform(get(url).session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.page.totalElements");
    }
}
