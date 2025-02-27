package com.hostfully.service;

import com.hostfully.entity.Block;
import com.hostfully.entity.Place;
import com.hostfully.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BlockServiceTest {

    @InjectMocks
    private BlockService blockService;

    @Mock
    private BlockRepository blockRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBlock() {
        Place property = new Place(1L, "Test Property", "Test Location");
        Block block = new Block(1L, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 7), property);
        when(blockRepository.save(any(Block.class))).thenReturn(block);

        Block createdBlock = blockService.createBlock(block);

        assertNotNull(createdBlock);
        assertEquals(block.getStartDate(), createdBlock.getStartDate());
        assertEquals(block.getEndDate(), createdBlock.getEndDate());
    }

    @Test
    public void testUpdateBlock() {
        Block existingBlock = new Block();
        Block newBlock = new Block();
        newBlock.setStartDate(LocalDate.now());
        newBlock.setEndDate(LocalDate.now().plusDays(1));

        when(blockRepository.findById(anyLong())).thenReturn(Optional.of(existingBlock));
        when(blockRepository.save(any(Block.class))).thenReturn(newBlock);

        blockService.updateBlock(1L, newBlock);

        verify(blockRepository).findById(1L);
        verify(blockRepository).save(existingBlock);
    }

    @Test
    public void testDeleteBlock() {
        // Configura o mock para retornar um bloco quando findById for chamado
        Block block = new Block();
        when(blockRepository.findById(anyLong())).thenReturn(Optional.of(block));
        blockService.deleteBlock(1L);
    }

    @Test
    public void testDeleteBlockNotFound() {
        when(blockRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> blockService.deleteBlock(1L));
    }

    @Test
    public void testGetBlock() {
        Place property = new Place(1L, "Test Property", "Test Location");
        Block block = new Block(1L, LocalDate.now(), LocalDate.now().plusDays(1), property);
        when(blockRepository.findById(anyLong())).thenReturn(Optional.of(block));

        Block returnedblock = blockService.getBlock(1L);

        assertNotNull(returnedblock);
        assertEquals(1L, returnedblock.getId());
        assertEquals(LocalDate.now(), returnedblock.getStartDate());
        assertEquals(LocalDate.now().plusDays(1), returnedblock.getEndDate());
    }

    @Test
    public void testGetAllBlocks() {
        Place property1 = new Place(1L, "Test Property1", "Test Location1");
        Place property2 = new Place(1L, "Test Property2", "Test Location2");

        List<Block> blocks = List.of(
                new Block(1L, LocalDate.now(), LocalDate.now().plusDays(1), property1),
                new Block(2L, LocalDate.now(), LocalDate.now().plusDays(2), property2)
        );
        when(blockRepository.findAll()).thenReturn(blocks);

        List<Block> foundBlocks = blockService.getAllBlocks();
        assertNotNull(foundBlocks);
        assertEquals(2, foundBlocks.size());
    }

    @Test
    public void testGetAllBlocksNotFound() {
        when(blockRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blockService.getAllBlocks();
        });

        String expectedMessage = "No blocks found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
