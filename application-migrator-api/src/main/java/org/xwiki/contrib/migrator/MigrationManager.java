/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.migrator;

import java.util.Set;

import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.migrator.job.AbstractMigrationJobStatus;
import org.xwiki.extension.ExtensionId;

/**
 * This is the central manager for migrations.
 *
 * @version $Id$
 * @since 1.0
 */
@Role
public interface MigrationManager
{
    /**
     * Verifies if the given extension has one or more pending migrations.
     *
     * @param extensionId the extension that should be used
     * @throws MigrationException if an error happens
     * @return true if at least one migration is available
     */
    boolean hasAvailableMigrations(ExtensionId extensionId) throws MigrationException;

    /**
     * Get a set of the available migrations. An available migration is a migration that has not been applied yet.
     *
     * @param extensionId the extension to look for
     * @throws MigrationException if an error happens
     * @return the available migrations
     */
    Set<AbstractMigrationDescriptor> getAvailableMigrations(ExtensionId extensionId) throws MigrationException;

    /**
     * Applies the given migrations.
     *
     * @param migrationDescriptors the migrations to apply
     * @throws MigrationException if an error happens
     * @return the job status of the migration
     */
    AbstractMigrationJobStatus applyMigrations(Set<AbstractMigrationDescriptor> migrationDescriptors)
            throws MigrationException;

    /**
     * Apply all the available migrations for the given extension ID.
     *
     * @param extensionId the extension (and its version) that should be migrated
     * @throws MigrationException if an error happens
     * @return the job status of the migration
     */
    AbstractMigrationJobStatus applyMigrationsForVersion(ExtensionId extensionId) throws MigrationException;
}