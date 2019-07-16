package com.radixdlt.constraintmachine;

import java.util.stream.Stream;
import com.radixdlt.atoms.ParticleGroup;

/**
 * Temporary helper class in transforming constraint to validator.
 * Should NOT be used by higher level abstractions.
 *
 * TODO: remove at some point after refactor of constraint machine
 */
public interface ConstraintProcedure {
	Stream<ProcedureError> validate(ParticleGroup particleGroup, AtomMetadata metadata);
}