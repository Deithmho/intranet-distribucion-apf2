package com.dwi.intranetdistribucion;

import com.dwi.intranetdistribucion.dto.request.CatalogoDTO.*;
import com.dwi.intranetdistribucion.repository.*;
import com.dwi.intranetdistribucion.service.InventarioService;
import com.fasterxml.jackson.databind.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @Testcontainers
class IntranetDistribucionBackApplicationTests {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17.11-alpine");
    static final String PASSWORD=UUID.randomUUID().toString();
    static final byte[] KEY=UUID.randomUUID().toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    @DynamicPropertySource static void configure(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url",POSTGRES::getJdbcUrl);r.add("spring.datasource.username",POSTGRES::getUsername);r.add("spring.datasource.password",POSTGRES::getPassword);
        r.add("app.jwt.secret",() -> Base64.getEncoder().encodeToString(KEY));r.add("app.bootstrap.enabled",() -> true);r.add("app.bootstrap.email",() -> "admin@example.test");r.add("app.bootstrap.password",() -> PASSWORD);
    }
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioRepository users;
    @Autowired RolRepository roles;
    @Autowired ExistenciaRepository stock;
    @Autowired MovimientoRepository movements;
    @Autowired InventarioService inventory;
    String token;
    String suffix;
    @BeforeEach void login() throws Exception { token=login("admin@example.test",PASSWORD);suffix=UUID.randomUUID().toString().substring(0,8); }
    String login(String email,String password) throws Exception {
        var result=mvc.perform(post("/api/v1/auth/login").contentType("application/json").content(json.writeValueAsString(Map.of("email",email,"password",password)))).andExpect(status().isOk()).andReturn();
        return json.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }
    JsonNode create(String resource,Object payload) throws Exception {
        return json.readTree(mvc.perform(post("/api/v1/"+resource).header("Authorization","Bearer "+token).contentType("application/json").content(json.writeValueAsString(payload))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
    }
    long category() throws Exception { return create("categorias",Map.of("nombre","Bebidas-"+suffix)).get("id").asLong(); }
    long product(long category) throws Exception { return create("productos",Map.of("codigo","P-"+suffix,"nombre","Producto de prueba","unidadMedida","caja","stockMinimo",10,"categoriaId",category)).get("id").asLong(); }
    long[] warehouses() throws Exception {
        long sede=create("sedes",Map.of("codigo","S-"+suffix,"nombre","Lima","direccion","Dirección sintética")).get("id").asLong();
        long a=create("almacenes",Map.of("codigo","A-"+suffix,"nombre","Origen","sedeId",sede)).get("id").asLong();
        long b=create("almacenes",Map.of("codigo","B-"+suffix,"nombre","Destino","sedeId",sede)).get("id").asLong();return new long[]{a,b,sede};
    }
    @Test void categoryCrud() throws Exception {
        long id=category();
        mvc.perform(get("/api/v1/categorias/"+id).header("Authorization","Bearer "+token)).andExpect(status().isOk());
        mvc.perform(put("/api/v1/categorias/"+id).header("Authorization","Bearer "+token).contentType("application/json").content(json.writeValueAsString(Map.of("nombre","Editada-"+suffix)))).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Editada-"+suffix));
        mvc.perform(delete("/api/v1/categorias/"+id).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/categorias/"+id).header("Authorization","Bearer "+token)).andExpect(status().isNotFound());
    }
    @Test void productJpqlAndDeleteRules() throws Exception {
        long c=category();long p=product(c);
        mvc.perform(get("/api/v1/productos").param("categoriaId",""+c).param("texto",suffix).header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(p));
        mvc.perform(delete("/api/v1/categorias/"+c).header("Authorization","Bearer "+token)).andExpect(status().isConflict());
        mvc.perform(put("/api/v1/productos/"+p).header("Authorization","Bearer "+token).contentType("application/json").content(json.writeValueAsString(Map.of("codigo","P-"+suffix,"nombre","Actualizado","unidadMedida","caja","stockMinimo",5,"categoriaId",c)))).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/productos/"+p).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());
    }
    @Test void warehouseIntegrityAndCrud() throws Exception {
        long[] w=warehouses();
        mvc.perform(delete("/api/v1/sedes/"+w[2]).header("Authorization","Bearer "+token)).andExpect(status().isConflict());
        mvc.perform(put("/api/v1/almacenes/"+w[0]).header("Authorization","Bearer "+token).contentType("application/json").content(json.writeValueAsString(Map.of("codigo","A-"+suffix,"nombre","Actualizado","sedeId",w[2])))).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/almacenes/"+w[0]).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/almacenes/"+w[1]).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/sedes/"+w[2]).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());
    }
    @Test void missingTokenAndPublicStatus() throws Exception {
        mvc.perform(get("/api/v1/productos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/public/estado")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/auth/register").contentType("application/json").content("{}")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/productos").header("Authorization","Bearer "+token)).andExpect(status().isOk());
    }
    @Test void malformedExpiredAndInvalidSignatureAre401() throws Exception {
        String expired=Jwts.builder().subject("admin@example.test").expiration(new Date(1000)).signWith(Keys.hmacShaKeyFor(KEY)).compact();
        String other=Jwts.builder().subject("admin@example.test").expiration(new Date(System.currentTimeMillis()+60000)).signWith(Keys.hmacShaKeyFor(UUID.randomUUID().toString().getBytes())).compact();
        for(String value:List.of("malformado",expired,other)) mvc.perform(get("/api/v1/productos").header("Authorization","Bearer "+value)).andExpect(status().isUnauthorized());
    }
    long user(String email,String role) throws Exception { return create("usuarios",Map.of("nombre","Usuario","apellido","Prueba","email",email,"password",PASSWORD,"rolId",roles.findByNombre(role).orElseThrow().getId())).get("id").asLong(); }
    @Test void auditorReadOnlyAndPasswordsNotExposed() throws Exception {
        String email="auditor-"+suffix+"@example.test";user(email,"AUDITOR");String auditor=login(email,PASSWORD);
        mvc.perform(get("/api/v1/productos").header("Authorization","Bearer "+auditor)).andExpect(status().isOk());
        mvc.perform(post("/api/v1/categorias").header("Authorization","Bearer "+auditor).contentType("application/json").content("{\"nombre\":\"Prohibida\"}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/usuarios").header("Authorization","Bearer "+auditor)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/usuarios").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].password").doesNotExist());
        assertThat(users.findByEmail(email).orElseThrow().getPassword()).startsWith("$2").isNotEqualTo(PASSWORD);
    }
    @Test void duplicateAndInvalidInputs() throws Exception {
        category();
        mvc.perform(post("/api/v1/categorias").header("Authorization","Bearer "+token).contentType("application/json").content(json.writeValueAsString(Map.of("nombre","Bebidas-"+suffix)))).andExpect(status().isConflict());
        mvc.perform(post("/api/v1/productos").header("Authorization","Bearer "+token).contentType("application/json").content("{}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/productos/no-es-numero").header("Authorization","Bearer "+token)).andExpect(status().isBadRequest());
    }
    @Test void transferConservesStockAndRegistersAudit() throws Exception {
        long p=product(category());long[] w=warehouses();inventory.entrada(new EntradaRequest(p,w[0],20,"Ingreso de prueba"),"admin@example.test");
        inventory.trasladar(new TrasladoRequest(p,w[0],w[1],7,"Traslado de prueba"),"admin@example.test");
        assertThat(stock.findByProductoIdAndAlmacenId(p,w[0]).orElseThrow().getCantidad()).isEqualTo(13);
        assertThat(stock.findByProductoIdAndAlmacenId(p,w[1]).orElseThrow().getCantidad()).isEqualTo(7);
        mvc.perform(get("/api/v1/inventario").param("almacenId",""+w[1]).param("critico","true").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].cantidad").value(7));
        assertThat(movements.findAll().stream().filter(x -> x.getProducto().getId().equals(p)).count()).isEqualTo(2);
    }
    @Test void insufficientStockDoesNotChangeDatabase() throws Exception {
        long p=product(category());long[] w=warehouses();inventory.entrada(new EntradaRequest(p,w[0],5,"Ingreso"),"admin@example.test");long count=movements.count();
        assertThatThrownBy(() -> inventory.trasladar(new TrasladoRequest(p,w[0],w[1],6,"Salida"),"admin@example.test")).isInstanceOf(IllegalStateException.class);
        assertThat(stock.findByProductoIdAndAlmacenId(p,w[0]).orElseThrow().getCantidad()).isEqualTo(5);assertThat(movements.count()).isEqualTo(count);
    }
    @Test void rollbackReversesAnAlreadyFlushedStockUpdate() throws Exception {
        long p=product(category());long[] w=warehouses();inventory.entrada(new EntradaRequest(p,w[0],20,"Ingreso"),"admin@example.test");long count=movements.count();
        assertThatThrownBy(() -> inventory.trasladar(new TrasladoRequest(p,w[0],Long.MAX_VALUE,7,"Destino inválido"),"admin@example.test")).isInstanceOf(com.dwi.intranetdistribucion.exception.ResourceNotFoundException.class);
        assertThat(stock.findByProductoIdAndAlmacenId(p,w[0]).orElseThrow().getCantidad()).isEqualTo(20);assertThat(movements.count()).isEqualTo(count);
    }
    @Test void concurrentTransfersCannotOverdrawStock() throws Exception {
        long p=product(category());long[] w=warehouses();inventory.entrada(new EntradaRequest(p,w[0],10,"Ingreso"),"admin@example.test");
        var pool=Executors.newFixedThreadPool(2);var latch=new CountDownLatch(1);
        Callable<Boolean> task=() -> {latch.await();try {inventory.trasladar(new TrasladoRequest(p,w[0],w[1],7,"Concurrente"),"admin@example.test");return true;}catch(IllegalStateException ex){return false;}};
        try {var a=pool.submit(task);var b=pool.submit(task);latch.countDown();assertThat(List.of(a.get(20,TimeUnit.SECONDS),b.get(20,TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);}finally{pool.shutdownNow();}
        assertThat(stock.findByProductoIdAndAlmacenId(p,w[0]).orElseThrow().getCantidad()).isEqualTo(3);
        assertThat(stock.findByProductoIdAndAlmacenId(p,w[1]).orElseThrow().getCantidad()).isEqualTo(7);
    }
    @Test void failedLoginCounterPersistsAndLocksAtFive() throws Exception {
        String email="bloqueo-"+suffix+"@example.test";user(email,"AUDITOR");
        for(int i=1;i<=5;i++) mvc.perform(post("/api/v1/auth/login").contentType("application/json").content(json.writeValueAsString(Map.of("email",email,"password",UUID.randomUUID().toString())))).andExpect(status().is(i<5?401:423));
        var u=users.findByEmail(email).orElseThrow();assertThat(u.getIntentosFallidos()).isEqualTo(5);assertThat(u.getEsBloqueado()).isTrue();
        mvc.perform(post("/api/v1/auth/login").contentType("application/json").content(json.writeValueAsString(Map.of("email",email,"password",PASSWORD)))).andExpect(status().isLocked());
    }
    @Test void inactiveUserCannotReuseIssuedToken() throws Exception {
        String email="inactivo-"+suffix+"@example.test";user(email,"AUDITOR");String issued=login(email,PASSWORD);
        var u=users.findByEmail(email).orElseThrow();u.setEsActivo(false);users.saveAndFlush(u);
        mvc.perform(get("/api/v1/productos").header("Authorization","Bearer "+issued)).andExpect(status().isUnauthorized());
    }
    @Test void userAndRoleCrudRejectInvalidPermissions() throws Exception {
        long id=user("crud-"+suffix+"@example.test","AUDITOR");
        mvc.perform(put("/api/v1/usuarios/"+id).header("Authorization","Bearer "+token).contentType("application/json").content(json.writeValueAsString(Map.of("nombre","Editado","apellido","Prueba","email","crud-"+suffix+"@example.test","password",PASSWORD,"rolId",roles.findByNombre("AUDITOR").orElseThrow().getId())))).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/usuarios/"+id).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());
        mvc.perform(post("/api/v1/roles").header("Authorization","Bearer "+token).contentType("application/json").content(json.writeValueAsString(Map.of("nombre","PRUEBA","permisoIds",List.of(Long.MAX_VALUE))))).andExpect(status().isBadRequest());
    }
}
