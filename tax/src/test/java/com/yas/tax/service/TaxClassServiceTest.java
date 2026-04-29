package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaxClassServiceTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private TaxClassService taxClassService;

    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = TaxClass.builder().build();
        taxClass.setId(1L);
        taxClass.setName("Standard");
    }

    @Test
    void findAllTaxClasses_shouldReturnListOfTaxClassVm() {
        when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Standard");
    }

    @Test
    void findById_whenExists_shouldReturnTaxClassVm() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Standard");
    }

    @Test
    void findById_whenNotFound_shouldThrowNotFoundException() {
        when(taxClassRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.findById(99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_whenNameNotDuplicated_shouldSaveAndReturn() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Standard");
        when(taxClassRepository.existsByName("Standard")).thenReturn(false);
        when(taxClassRepository.save(any())).thenReturn(taxClass);

        TaxClass result = taxClassService.create(postVm);

        assertThat(result.getName()).isEqualTo("Standard");
    }

    @Test
    void create_whenNameDuplicated_shouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Standard");
        when(taxClassRepository.existsByName("Standard")).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.create(postVm))
            .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void update_whenExists_shouldUpdate() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Updated");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated", 1L)).thenReturn(false);

        taxClassService.update(postVm, 1L);

        verify(taxClassRepository).save(taxClass);
        assertThat(taxClass.getName()).isEqualTo("Updated");
    }

    @Test
    void update_whenNotFound_shouldThrowNotFoundException() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Updated");
        when(taxClassRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taxClassService.update(postVm, 99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenDuplicateName_shouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("TC1", "Other");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Other", 1L)).thenReturn(true);

        assertThatThrownBy(() -> taxClassService.update(postVm, 1L))
            .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void delete_whenExists_shouldDelete() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrowNotFoundException() {
        when(taxClassRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taxClassService.delete(99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPageableTaxClasses_shouldReturnPaginatedResult() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<TaxClass> page = new PageImpl<>(List.of(taxClass), pageable, 1);
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 5);

        assertThat(result.taxClassContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}
