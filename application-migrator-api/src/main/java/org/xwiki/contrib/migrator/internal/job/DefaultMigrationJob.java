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
package org.xwiki.contrib.migrator.internal.job;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.contrib.migrator.AbstractMigrationDescriptor;
import org.xwiki.contrib.migrator.MigrationException;
import org.xwiki.contrib.migrator.MigrationExecutor;
import org.xwiki.contrib.migrator.MigrationHistoryStore;
import org.xwiki.contrib.migrator.MigrationStatus;
import org.xwiki.contrib.migrator.job.AbstractMigrationJob;
import org.xwiki.contrib.migrator.job.AbstractMigrationJobRequest;
import org.xwiki.contrib.migrator.job.AbstractMigrationJobStatus;
import org.xwiki.job.Job;
import org.xwiki.job.event.status.JobStatus;

/**
 * This is the default implementation of {@link AbstractMigrationJob}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(AbstractMigrationJob.JOB_TYPE)
public class DefaultMigrationJob extends AbstractMigrationJob
{
    @Inject
    private MigrationHistoryStore migrationHistoryStore;

    @Override
    protected AbstractMigrationJobStatus createNewStatus(AbstractMigrationJobRequest request)
    {
        Job currentJob = this.jobContext.getCurrentJob();
        JobStatus currentJobStatus = currentJob != null ? currentJob.getStatus() : null;
        return new DefaultMigrationJobStatus(AbstractMigrationJob.JOB_TYPE, request, currentJobStatus,
                this.observationManager, this.loggerManager);
    }

    @Override
    protected void runInternal() throws Exception
    {
        AbstractMigrationDescriptor migrationDescriptor = request.getMigrationDescriptor();

        // Fetch the executor that could be used for the migration
        try {
            MigrationExecutor executor = componentManager.getInstance(
                    new DefaultParameterizedType(null, MigrationExecutor.class,
                                    migrationDescriptor.getClass()));

            MigrationStatus migrationStatus = executor.execute(migrationDescriptor);
            status.setMigrationStatus(migrationStatus);

            if (migrationStatus.getStatus().equals(MigrationStatus.Status.SUCCESS)) {
                migrationHistoryStore.addAppliedMigration(migrationDescriptor);
            }
        } catch (ComponentLookupException e) {
            throw new MigrationException(String.format(
                    "Failed to retrieve a MigrationExecutor for the descriptor type [%s]",
                    request.getMigrationDescriptor().getClass()), e);
        }
    }
}
