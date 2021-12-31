package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith(MockitoExtension.class) indica que se inicialicen los mosk
 */
@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    /**
     * @Mock indica que cree un mock
     * lenient = true > Lenient stubs bypass “strict stubbing” validation rules.
     */
    @Mock(lenient = true)
    SpecialtyRepository specialtyRepository;

    /**
     * @InjectMocks indica que cree una instancia de la clase y le inyecte los Mocks que necesite.
     * Se aplica sobre clases, no sobre interfaces
     */
    @InjectMocks
    SpecialitySDJpaService service;

    @Test
    void findByIdTest() {
        Speciality speciality = new Speciality();
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(speciality));

        Speciality foundSpecialty = service.findById(1L);

        assertThat(foundSpecialty).isNotNull();
        verify(specialtyRepository, times(1)).findById(1L);
    }

    @Test
    void findByIdBDDTest() {
        Speciality speciality = new Speciality();
        //given
        given(specialtyRepository.findById(1L)).willReturn(Optional.of(speciality));

        //when
        Speciality foundSpecialty = service.findById(1L);

        //then
        assertThat(foundSpecialty).isNotNull();
        verify(specialtyRepository).findById(1L);
        then(specialtyRepository).should().findById(anyLong());
        then(specialtyRepository).should(times(1)).findById(anyLong());
        then(specialtyRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteById() {
        service.deleteById(1l);

        /**
         * Verifica que se ha ejecutado el método de la clase Mockeada
         */
        verify(specialtyRepository).deleteById(any());
        /**
         * Verifica que se ha ejecutado el método de la clase Mockeada con ese argumento
         */
        verify(specialtyRepository).deleteById(1L);
        verify(specialtyRepository).deleteById(anyLong());
    }

    @Test
    void testDelete() {
        service.delete(new Speciality());
        service.delete(new Speciality());
        verify(specialtyRepository, times(2)).delete(any());
        verify(specialtyRepository, atLeastOnce()).delete(any());
        verify(specialtyRepository, atLeast(2)).delete(any());
        verify(specialtyRepository, atMost(2)).delete(any());

        then(specialtyRepository).should(times(2)).delete(any());
        then(specialtyRepository).should(atLeastOnce()).delete(any());
        then(specialtyRepository).should(atLeast(2)).delete(any());
        then(specialtyRepository).should(atMost(2)).delete(any());
    }

    @Test
    void testDeleteByObject() {
        Speciality speciality = new Speciality();
        service.delete(speciality);
        verify(specialtyRepository, atMost(2)).delete(any(Speciality.class));
    }


    @Test
    void testNever() {
        verify(specialtyRepository, never()).findAll();
        verifyNoInteractions(specialtyRepository);
        then(specialtyRepository).should(never()).findAll();
        then(specialtyRepository).shouldHaveNoInteractions();
    }

    @Test
    void testSaveLambda() {
        //given
        final String MATCH_ME = "MATCH_ME";
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);

        Speciality savedSpecialty = new Speciality();
        savedSpecialty.setId(1L);

        //need mock to only return on match MATCH_ME string
        given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME))))
                .willReturn(savedSpecialty);
        //given(specialtyRepository.save(any())).willReturn(savedSpecialty);

        //when
        Speciality returnedSpecialty = service.save(speciality);

        //then
        assertThat(returnedSpecialty.getId()).isEqualTo(1L);
        // Comprueba que el argumento es una Speciality con la Descripción "MATCH_ME"
        verify(specialtyRepository).save(argThat(argument -> argument.getDescription().equals(MATCH_ME)));
    }

    @Test
    void testSaveLambdaNoMatch() {
        //given
        final String MATCH_ME = "MATCH_ME";
        Speciality speciality = new Speciality();
        speciality.setDescription("Not a match");

        Speciality savedSpecialty = new Speciality();
        savedSpecialty.setId(1L);

        //need mock to only return on match MATCH_ME string
        // lenient() > Lenient stubs bypass “strict stubbing” validation rules.
        // lenient().when(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).thenReturn(savedSpecialty);
        when(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME))))
                .thenReturn(savedSpecialty);

        //given(specialtyRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpecialty);

        //when
        Speciality returnedSpecialty = service.save(speciality);

        //then
        assertThat(returnedSpecialty).isNull();
        assertNull(returnedSpecialty);
    }

    @Test
    void testSaveAllLambda() {
        //given
        final String MATCH_ME = "MATCH_ME";
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);

        List<Speciality> specialityListIn = List.of(speciality);

        Speciality savedSpecialty = new Speciality();
        savedSpecialty.setId(1L);
        List<Speciality> specialityListOut = List.of(savedSpecialty);

        //need mock to only return on match MATCH_ME string
        given(specialtyRepository.saveAll(anyList())).willReturn(specialityListOut);

        //when
        List<Speciality> returnedSpecialtyList = (List<Speciality>) specialtyRepository.saveAll(specialityListIn);

        //then
        assertThat(returnedSpecialtyList).size().isPositive();
        assertThat(returnedSpecialtyList).contains(savedSpecialty);
        // Comprueba que el argumento es una Speciality con la Descripción "MATCH_ME"
        verify(specialtyRepository).saveAll(argThat(argument -> ((List<Speciality>) argument).size() == 1));
        verify(specialtyRepository).saveAll(argThat(argument -> ((List<Speciality>) argument).contains(speciality)));
        verify(specialtyRepository).saveAll(argThat(argument -> ((List<Speciality>) argument)
                .stream()
                .allMatch((spe) -> spe.getDescription().equals(MATCH_ME))))
        ;
    }

}