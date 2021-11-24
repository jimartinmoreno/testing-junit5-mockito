package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @InjectMocks
    OwnerController ownerController;

    @Mock
    OwnerService ownerService;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    /**
     * @Captor annotation to create an ArgumentCaptor instance.
     */
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    //@BeforeEach
    void setUp() {
        /**
         * willAnswer -> Me permite definir distintas respuesta a la invocación de un método para reproducir todos los escenarios
         * y no tener que escribir cada caso con willReturn, evita duplicidad de código
         */
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willAnswer(invocation -> {
                    List<Owner> owners = new ArrayList<>();
                    Arrays.stream(invocation.getArguments()).toList().forEach(System.out::println);
                    String name = invocation.getArgument(0);
                    if (name.equals("%Buck%")) {
                        owners.add(new Owner(1l, "Joe", "Buck"));
                        return owners;
                    } else if (name.equals("%DontFindMe%")) {
                        return owners;
                    } else if (name.equals("%FindMe%")) {
                        owners.add(new Owner(1l, "Joe", "Buck"));
                        owners.add(new Owner(2l, "Joe2", "Buck2"));
                        return owners;
                    }
                    throw new RuntimeException("Invalid Argument");
                });
    }

    @Test
    void processFindFormWildcardNotFound() {
        setUp();
        //given
        Owner owner = new Owner(1l, "Joe", "DontFindMe");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, model);
        //then
        assertThat("%DontFindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processFindFormWildcardSeveralFoundCheckInteractionOrder() {
        setUp();
        //given
        Owner owner = new Owner(1l, "Joe", "FindMe");
        /**
         * InOrder -> Allows verification in order.
         */
        InOrder inOrder = inOrder(ownerService, model);
        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, model);
        //then
        assertThat("%FindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);

        // inorder asserts evaluates that the executions are following the assertions order
        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        inOrder.verify(model, times(1)).addAttribute(anyString(), anyList());
        verifyNoMoreInteractions(model);
    }

    /**
     * Test en el que el argument captor lo definimos de manera programatica
     */
    @Test
    void processFindFormWildcardString() {
        //setUp();
        Owner owner = new Owner(1l, "Joe", "Buck");
        List<Owner> ownerList = List.of(owner);
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        //given
        given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList);
        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, model);
        //then
        verify(ownerService).findAllByLastNameLike(captor.capture());
        verifyNoInteractions(model);
        verify(model, times(0)).addAttribute(anyString(), any(Object.class));
        assertThat("%Buck%").isEqualToIgnoringCase(captor.getValue());
    }

    /**
     * Test en el que el argument captor lo con anotaciones
     */
    @Test
    void processFindFormWildcardStringAnnotation() {
        Owner owner = new Owner(1l, "Joe", "Buck");
        List<Owner> ownerList = List.of(owner);
        //given
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);
        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, model);
        //then
        assertThat("%Buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
    }

    @Test
    void processFindFormWildcardStringAnnotation2() {
        Owner owner = new Owner(1l, "Joe", "Buck");
        List<Owner> ownerList = List.of(owner);
        //given
        given(ownerService.findAllByLastNameLike(any())).willReturn(ownerList);
        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, model);

        verify(ownerService).findAllByLastNameLike(stringArgumentCaptor.capture());
        System.out.println("viewName = " + viewName);
        System.out.println("stringArgumentCaptor = " + stringArgumentCaptor.getValue());
        //then
        assertThat("%Buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
    }

    @Test
    void processCreationFormTest() {

        Owner savedOwner = new Owner(5L, "Nacho", "Martin");
        when(ownerService.save(any(Owner.class))).thenReturn(savedOwner);
        when(bindingResult.hasErrors()).thenReturn(false);

        String controllerResult = ownerController.processCreationForm(savedOwner, bindingResult);

        assertThat(controllerResult).isNotNull().isEqualTo("redirect:/owners/" + savedOwner.getId());
        verify(ownerService).save(any(Owner.class));
        verify(ownerService).save(savedOwner);
        verify(ownerService, times(1)).save(any(Owner.class));
    }

    @Test
    void processCreationFormFailTest() {

        Owner ownerCreated = new Owner(5L, "Nacho", "Martin");
        given(bindingResult.hasErrors()).willReturn(true);

        String controllerResult = ownerController.processCreationForm(ownerCreated, bindingResult);

        assertThat(controllerResult).isNotNull().isEqualTo("owners/createOrUpdateOwnerForm");
        then(ownerService).should(times(0)).save(any());
        then(ownerService).shouldHaveNoInteractions();
        then(ownerService).should(never()).save(any(Owner.class));
    }
}