package db;

import domain.colaboraciones.DistribucionViandas;
import domain.colaboraciones.MotivoDistribucion;
import domain.comunicaciones.Contacto;
import domain.comunicaciones.TipoDeContacto;
import domain.heladera.Heladera;
import domain.usuario.colaborador.Humana;
import domain.usuario.tecnico.Tecnico;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import repositorios.RepoGenerico;
import repositorios.colaboradores.RepoHumana;
import repositorios.heladera.RepoHeladera;
import repositorios.tecnico.RepoTecnicos;

import java.util.List;

public class ContextTest implements SimplePersistenceTest {

    void contextUpWithTransaction() throws Exception {
        RepoGenerico repoGenerico = new RepoGenerico();
        RepoHumana repoHumana = new RepoHumana();
        RepoHeladera repoHeladera = new RepoHeladera();

        Heladera heladeraLlena = repoHeladera.buscarPorNombre("Heladera UTN - CABA");
        Heladera heladeraVacia = repoHeladera.buscarPorNombre("Heladera UTN - CABA");

        // Humana colaborador1 = repoHumana.buscarPorUsername("sofiamartinez");
        // DistribucionViandas distribucionViandas1 = new DistribucionViandas(5,
        // heladeraLlena, heladeraVacia, MotivoDistribucion.FALTADEVIANDAS,
        // colaborador1);

        DistribucionViandas distribucionViandas2 = new DistribucionViandas(3, heladeraLlena, heladeraVacia,
                MotivoDistribucion.FALTADEVIANDAS);

        withTransaction(() -> {
            // repoGenerico.persist(distribucionViandas1);
            repoGenerico.persist(distribucionViandas2);
        });
    }

    void contactosTransaction() throws Exception {
        RepoTecnicos repoTecnicos = new RepoTecnicos();

        List<Tecnico> tecnicos = repoTecnicos.buscarTodos();

        Contacto contactoTecnico1 = new Contacto(TipoDeContacto.EMAIL, "luciano.rocchetta@gmail.com", tecnicos.get(0));
        Contacto contactoTecnico2 = new Contacto(TipoDeContacto.EMAIL, "mariana.lopez@gmail.com", tecnicos.get(1));
        Contacto contactoTecnico3 = new Contacto(TipoDeContacto.EMAIL, "carlos.ramirez@gmail.com", tecnicos.get(2));
        Contacto contactoTecnico4 = new Contacto(TipoDeContacto.EMAIL, "bruno.sartori@gmail.com", tecnicos.get(3));
        Contacto contactoTecnico5 = new Contacto(TipoDeContacto.EMAIL, "jorge.gomez@gmail.com", tecnicos.get(4));
        Contacto contactoTecnico6 = new Contacto(TipoDeContacto.EMAIL, "laura.fernandez@gmail.com", tecnicos.get(5));
        Contacto contactoTecnico7 = new Contacto(TipoDeContacto.EMAIL, "diego.sanchez@gmail.com", tecnicos.get(6));
        Contacto contactoTecnico8 = new Contacto(TipoDeContacto.EMAIL, "sofia.gutierrez@gmail.com", tecnicos.get(7));
        Contacto contactoTecnico9 = new Contacto(TipoDeContacto.EMAIL, "pablo.rojas@gmail.com", tecnicos.get(8));
        Contacto contactoTecnico10 = new Contacto(TipoDeContacto.EMAIL, "valeria.castro@gmail.com", tecnicos.get(9));

        withTransaction(() -> {
            repoTecnicos.persist(contactoTecnico1);
            repoTecnicos.persist(contactoTecnico2);
            repoTecnicos.persist(contactoTecnico3);
            repoTecnicos.persist(contactoTecnico4);
            repoTecnicos.persist(contactoTecnico5);
            repoTecnicos.persist(contactoTecnico6);
            repoTecnicos.persist(contactoTecnico7);
            repoTecnicos.persist(contactoTecnico8);
            repoTecnicos.persist(contactoTecnico9);
            repoTecnicos.persist(contactoTecnico10);
        });
    }

    public static void main(String[] args) throws Exception {
        ContextTest contextTest = new ContextTest();
        contextTest.contactosTransaction();
    }
}