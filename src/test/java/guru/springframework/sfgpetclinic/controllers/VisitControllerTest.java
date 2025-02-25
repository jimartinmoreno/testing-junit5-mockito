package guru.springframework.sfgpetclinic.controllers;


import guru.springframework.sfgpetclinic.model.Pet;
import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.services.VisitService;
import guru.springframework.sfgpetclinic.services.map.PetMapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

    @Mock
    VisitService visitService;

    /**
     *  @Spy proporciona la implementación real en lugar de un mock y no premite realizar verificaciones sobre su ejecución
     */
    @Spy //@Mock
    PetMapService petService;

    @InjectMocks
    VisitController visitController;

    @Test
    void loadPetWithVisit() {
        //given
        Map<String, Object> model = new HashMap<>();
        Pet pet = new Pet(12L);
        Pet pet3 = new Pet(3L);

        petService.save(pet);
        petService.save(pet3);

        /**
         * En el caso de usar @Spy no es necesario ni given(BDD) ni when (Mockito)
         */
        //given(petService.findById(anyLong())).willCallRealMethod(); //.willReturn(pet);

        //when
        Visit visit = visitController.loadPetWithVisit(12L, model);

        //then
        assertThat(visit).isNotNull();
        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isEqualTo(12L);

        //Mockito
        verify(petService, times(1)).findById(anyLong());
        //BDD
        then(petService).should(times(1)).findById(anyLong());
    }

    @Test
    void loadPetWithVisitWithStubbing() {
        //given
        Map<String, Object> model = new HashMap<>();
        Pet pet = new Pet(12L);
        Pet pet3 = new Pet(3L);

        petService.save(pet);
        petService.save(pet3);

        // Reemplazamos el resultado real con uno que le indicamos nosotros usando BDD
        given(petService.findById(anyLong())).willReturn(pet3);

        //when
        Visit visit = visitController.loadPetWithVisit(12L, model);

        //then
        assertThat(visit).isNotNull();
        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isEqualTo(3L);
        //Mockito
        verify(petService, times(1)).findById(anyLong());
        //BDD
        then(petService).should(times(1)).findById(anyLong());
    }
}