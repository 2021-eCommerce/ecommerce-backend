package store.juin.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.juin.api.domain.RequestUtil;
import store.juin.api.domain.entity.Address;
import store.juin.api.service.command.AddressCommandService;
import store.juin.api.service.query.AddressQueryService;
import store.juin.api.service.query.PrincipalQueryService;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.juin.api.controller.EndPoint.*;
import static store.juin.api.domain.EntityUtil.makeAccount;
import static store.juin.api.domain.EntityUtil.makeAddress;
import static store.juin.api.domain.RequestUtil.makeCreateRequest;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class AddressApiControllerTest {
    private static final String RETRIEVE_ALL = API_ADDRESSES + "/all";
    private static final String PATH_VARIABLE = API_ADDRESSES + "/{addressId}";
    private static final String DOT = ".";

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AddressApiController sut;

    @Mock
    private AddressQueryService addressQueryService;
    @Mock
    private PrincipalQueryService principalQueryService;

    @Mock
    private AddressCommandService addressCommandService;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(sut)
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())   // request 본문을 예쁘게 출력
                        .withResponseDefaults(prettyPrint())) // response 본문을 예쁘게 출력
                .build();
    }

    @Nested
    @DisplayName(POST + API_ADDRESSES)
    class CreateTest {
        @Test
        @DisplayName("주소를 추가한다.")
        void createTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var request = makeCreateRequest(false);

            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            // when
            final ResultActions actual = mockMvc.perform(post(API_ADDRESSES)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsBytes(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + API_ADDRESSES + "/create"
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , requestFields(fieldWithPath("city").type(String.class).description("주소")
                                    , fieldWithPath("street").type(String.class).description("상세 주소")
                                    , fieldWithPath("zipCode").type(Integer.class).description("우편 번호")
                                    , fieldWithPath("defaultAddress").type(boolean.class).description("기본 주소"))

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    , fieldWithPath("data").type(Void.class).description("데이터")
                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("상세 주소"))
                    ));
        }
    }

    @Nested
    @DisplayName(GET + RETRIEVE_ALL)
    class RetrieveAllTest {
        @Test
        @DisplayName("한 유저의 모든 주소를 불러온다.")
        void retrieveAllTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var addressList = makeAddressList();
            given(addressQueryService.readAllByAccountId(account.getId())).willReturn(addressList);

            // when
            final ResultActions actual = mockMvc.perform(get(RETRIEVE_ALL)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + RETRIEVE_ALL
                            , requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"))

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    , fieldWithPath("data[].id").type(Integer.class).description("address id")
                                    , fieldWithPath("data[].city").type(String.class).description("주소")
                                    , fieldWithPath("data[].street").type(String.class).description("상세 주소")
                                    , fieldWithPath("data[].zipCode").type(Integer.class).description("우편 번호")
                                    , fieldWithPath("data[].defaultAddress").type(boolean.class).description("기본 주소")
                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("상세 주소"))
                    ));
        }
    }

    @Nested
    @DisplayName(GET + PATH_VARIABLE)
    class RetrieveOneTest {
        @Test
        @DisplayName("한 유저의 특정 주소를 불러온다.")
        void retrieveOneTest() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var addressId = 330L;
            given(addressQueryService.readByIdAndAccountId(addressId, account.getId()))
                    .willReturn(makeAddress(true));

            // when
            final ResultActions actual = mockMvc.perform(get(PATH_VARIABLE, addressId)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + PATH_VARIABLE
                            , pathParameters(
                                    parameterWithName("addressId").description("주소 아이디")
                            )

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    , fieldWithPath("data.id").type(Integer.class).description("address id")
                                    , fieldWithPath("data.city").type(String.class).description("주소")
                                    , fieldWithPath("data.street").type(String.class).description("상세 주소")
                                    , fieldWithPath("data.zipCode").type(Integer.class).description("우편 번호")
                                    , fieldWithPath("data.defaultAddress").type(boolean.class).description("기본 주소")
                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("상세 주소"))
                    ));
        }
    }

    @Nested
    @DisplayName(PUT + API_ADDRESSES)
    class UpdateTest {
        @Test
        @DisplayName("한 유저의 주소를 수정한다.")
        void updateTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var request = RequestUtil.makeAddressUpdateRequest(false);

            // when
            final ResultActions actual = mockMvc.perform(put(API_ADDRESSES)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal)
                    .content(objectMapper.writeValueAsString(request)));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + API_ADDRESSES
                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    , fieldWithPath("data").type(null).description("null")
                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("상세 주소"))
                    ));
        }
    }

    @Nested
    @DisplayName(DELETE + API_ADDRESSES)
    class DeleteTest {
        @Test
        @DisplayName("주소 하나를 삭제한다.")
        void deleteTest01() throws Exception {
            // given
            var principal = mock(Principal.class);
            var account = makeAccount();
            given(principalQueryService.readByPrincipal(principal)).willReturn(account);

            var addressId = 330L;
            var transaction = 1L;
            given(addressCommandService.remove(account.getId(), addressId)).willReturn(transaction);

            // when
            final ResultActions actual = mockMvc.perform(delete(PATH_VARIABLE, addressId)
                    .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer dXNlcjpzZWNyZXQ=")
                    .principal(principal));

            // then
            actual
                    .andExpect(status().isOk())
                    .andDo(document(DOT + PATH_VARIABLE
                            , pathParameters(
                                    parameterWithName("addressId").description("삭제할 주소 아이디")
                            )

                            , responseFields(fieldWithPath("apiStatus").type(String.class).description("api 요청에 대한 상태")
                                    , fieldWithPath("data").type(Long.class).description("삭제된 주소 개수")
                                    , fieldWithPath("timestamp").type(ZonedDateTime.class).description("API 요청 시각")
                                    , fieldWithPath("region").type(String.class).description("상세 주소"))
                    ));
        }
    }

    private List<Address> makeAddressList() {
        return List.of(
                Address.builder()
                        .id(1L)
                        .city("서울시")
                        .street("동작구")
                        .zipCode(18297)
                        .defaultAddress(true)
                        .build(),
                Address.builder()
                        .id(2L)
                        .city("서울시")
                        .street("강남구")
                        .zipCode(12345)
                        .defaultAddress(false)
                        .build()
        );
    }
}