package org.tcathebluecreper.totally_lib.dev_utils;

import com.lowdragmc.lowdraglib.gui.editor.ILDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.editor.annotation.LDLRegisterClient;
import com.lowdragmc.lowdraglib.gui.editor.data.IProject;
import com.lowdragmc.lowdraglib.gui.editor.ui.*;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.tcathebluecreper.totally_lib.TotallyLibrary;

import java.util.List;

@LDLRegisterClient(name = "editor.multiblock", group = "editor")
@OnlyIn(Dist.CLIENT)
public class MultiblockEditor extends Editor implements ILDLRegisterClient {

    public static final ConfigPanel.Tab BASIC = ConfigPanel.Tab.WIDGET;

    public MultiblockEditor() {
        super("");
    }

    public void initEditorViews() {
        this.toolPanel = new ToolPanel(this);
        this.toolPanel.setSizeWidth(150);
        this.configPanel = new ConfigPanel(this, List.of(BASIC));
        this.tabPages = new StringTabContainer(this);
        this.resourcePanel = new ResourcePanel(this);
        this.menuPanel = new MenuPanel(this);
        this.floatView = new WidgetGroup(0, 0, this.getSize().width, this.getSize().height);

        this.addWidget(this.tabPages);
        this.addWidget(this.toolPanel);
        this.addWidget(this.configPanel);
        this.addWidget(this.resourcePanel);
        this.addWidget(this.menuPanel);
        this.addWidget(this.floatView);
    }

    @Override
    public void loadProject(IProject project) {
        if(!(project instanceof MultiblockProject)) return;
        super.loadProject(project);
    }
}
