/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2013-2015 Denis Forveille (titou10.titou10@gmail.com)
 * Copyright (C) 2010-2016 Serge Rieder (serge@jkiss.org)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (version 2)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jkiss.dbeaver.ext.db2.model.app;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.jkiss.dbeaver.ext.db2.DB2Messages;
import org.jkiss.dbeaver.ext.db2.model.DB2DataSource;
import org.jkiss.dbeaver.model.admin.sessions.DBAServerSession;
import org.jkiss.dbeaver.model.admin.sessions.DBAServerSessionManager;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.views.session.AbstractSessionEditor;
import org.jkiss.dbeaver.ui.views.session.SessionManagerViewer;

import java.util.HashMap;
import java.util.Map;

/**
 * DB2 Application Editor
 * 
 * @author Denis Forveille
 */
public class DB2ServerApplicationEditor extends AbstractSessionEditor {
    private ForceApplicationAction forceApplicationAction;

    @Override
    public void createPartControl(Composite parent)
    {
        forceApplicationAction = new ForceApplicationAction();
        super.createPartControl(parent);
    }

    @Override
    protected SessionManagerViewer createSessionViewer(DBCExecutionContext executionContext, Composite parent) {
        return new SessionManagerViewer(this, parent, new DB2ServerApplicationManager((DB2DataSource) executionContext.getDataSource())) {

            @Override
            @SuppressWarnings("rawtypes")
            protected void contributeToToolbar(DBAServerSessionManager sessionManager, IContributionManager contributionManager)
            {
                contributionManager.add(forceApplicationAction);
                contributionManager.add(new Separator());
            }

            @Override
            protected void onSessionSelect(DBAServerSession session)
            {
                super.onSessionSelect(session);
                forceApplicationAction.setEnabled(session != null);
            }
        };
    }

    private class ForceApplicationAction extends Action {

        public ForceApplicationAction()
        {
            super(DB2Messages.editors_db2_application_editor_title_force_application, DBeaverIcons.getImageDescriptor(UIIcon.REJECT));
        }

        @Override
        public void run()
        {
            final DBAServerSession session = getSessionsViewer().getSelectedSession();
            final String action = DB2Messages.editors_db2_application_editor_action_force;
            if (UIUtils.confirmAction(getSite().getShell(), "Confirm force application",
                NLS.bind(DB2Messages.editors_db2_application_editor_confirm_action, action.toLowerCase(), session))) {
                Map<String, Object> options = new HashMap<>();
                getSessionsViewer().alterSession(session, options);
            }
        }
    }
}