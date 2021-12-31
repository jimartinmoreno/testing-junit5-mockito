package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.repositories.VisitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class VisitSDJpaServiceTest {

    @Mock
    VisitRepository visitRepository;

    @InjectMocks
    VisitSDJpaService service;

    @DisplayName("Test Find All")
    @Test
    void findAll() {
        Visit visit = new Visit();
        Set<Visit> visits = new HashSet<>();
        visits.add(visit);

        //Mockito
        when(visitRepository.findAll()).thenReturn(visits);
        //BDD
        given(visitRepository.findAll()).willReturn(visits);

        Set<Visit> foundVisits = service.findAll();
        //Mockito
        verify(visitRepository).findAll();
        //BDD
        then(visitRepository).should().findAll();
        assertThat(foundVisits).hasSize(1);

    }

    @Test
    void findById() {
        Visit visit = new Visit();
        when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));
        given(visitRepository.findById(anyLong())).willReturn(Optional.of(visit));

        Visit foundVisit = service.findById(1L);
        //Mockito
        verify(visitRepository).findById(anyLong());
        //BDD
        then(visitRepository).should().findById(anyLong());
        assertThat(foundVisit).isNotNull();
    }

    @Test
    void save() {
        Visit visit = new Visit();
        when(visitRepository.save(any(Visit.class))).thenReturn(visit);
        //given(visitRepository.save(any(Visit.class))).willReturn(visit);

        Visit savedVisit = service.save(new Visit());
        //Mockito
        verify(visitRepository).save(any(Visit.class));
        //BDD
        then(visitRepository).should().save(any(Visit.class));
        assertThat(savedVisit).isNotNull();
    }

    @Test
    void delete() {
        Visit visit = new Visit();
        service.delete(visit);
        //Mockito
        verify(visitRepository).delete(any(Visit.class));
        then(visitRepository).should().delete(any(Visit.class));
    }

    @Test
    void deleteById() {
        service.deleteById(1L);
        //Mockito
        verify(visitRepository).deleteById(anyLong());
        then(visitRepository).should().deleteById(anyLong());
    }

    @Test
    void testDoThrow() {
        doThrow(new RuntimeException("boom")).when(visitRepository).delete(any(Visit.class));
        assertThrows(RuntimeException.class, () -> visitRepository.delete(new Visit()));
        verify(visitRepository).delete(any());
    }

    @Test
    void testDeleteBDD() {
        willThrow(new RuntimeException("boom")).given(visitRepository).delete(any(Visit.class));
        assertThrows(RuntimeException.class, () -> visitRepository.delete(new Visit()));
        then(visitRepository).should().delete(any(Visit.class));
    }

    @Test
    void testFindByIDThrows() {
        doThrow(new RuntimeException("boom")).when(visitRepository).findById(anyLong());
        assertThrows(RuntimeException.class, () -> service.findById(1L));
        verify(visitRepository).findById(1L);
    }

    @Test
    void testFindByIDThrowsBDD() {
        given(visitRepository.findById(1L)).willThrow(new RuntimeException("boom"));
        assertThrows(RuntimeException.class, () -> service.findById(1L));
        then(visitRepository).should().findById(1L);
    }
}