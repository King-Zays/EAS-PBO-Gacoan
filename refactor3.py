import sys

# Update UITheme.java
theme_path = r'c:\Users\refia\koding\PBO\EAS-PBO-Gacoan\gacoan\UITheme.java'
with open(theme_path, 'r', encoding='utf-8') as f:
    tcontent = f.read()

tcontent = tcontent.replace('Color.decode("#F8F9FA")', 'Color.decode("#F3F4F6")')
tcontent = tcontent.replace('DEFAULT_RADIUS = 16', 'DEFAULT_RADIUS = 24')
tcontent = tcontent.replace('int shadowSize = 6', 'int shadowSize = 12')
tcontent = tcontent.replace('int shadowOffset = 2', 'int shadowOffset = 4')
tcontent = tcontent.replace('15.0 * (1.0 -', '6.0 * (1.0 -')

with open(theme_path, 'w', encoding='utf-8') as f:
    f.write(tcontent)


# Update GacoanApp.java
app_path = r'c:\Users\refia\koding\PBO\EAS-PBO-Gacoan\gacoan\GacoanApp.java'
with open(app_path, 'r', encoding='utf-8') as f:
    acontent = f.read()

# 1. Pill shape buttons
old_btn_paint = '''            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);'''
new_btn_paint = '''            int pillRadius = Math.min(getWidth(), getHeight());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), pillRadius, pillRadius);'''
acontent = acontent.replace(old_btn_paint, new_btn_paint)

# 2. Border abu-abu tipis on ModernTextField
old_tf_border = '''            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));'''
new_tf_border = '''            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));'''
acontent = acontent.replace(old_tf_border, new_tf_border)

# 3. Fix truncated Cart width & padding
acontent = acontent.replace('cartCardOuter.setPreferredSize(new Dimension(390, 0));', 'cartCardOuter.setPreferredSize(new Dimension(420, 0));')
acontent = acontent.replace('lblTotal.setFont(new Font("Segoe UI Black", Font.BOLD, 26));', 'lblTotal.setFont(new Font("Segoe UI Black", Font.BOLD, 22));')
acontent = acontent.replace('lblTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));', '')

# 4. Larger padding in Menu cards
old_card_border = '''            JPanel card = new JPanel(new BorderLayout(15, 12));
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(16, 16, 16, 16));'''
new_card_border = '''            JPanel card = new JPanel(new BorderLayout(15, 12));
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(24, 24, 24, 24));'''
acontent = acontent.replace(old_card_border, new_card_border)

# 5. Fix card height and text area sizing by using BoxLayout
old_options = '''            JPanel optionsPanel = new JPanel(new GridLayout(isFood ? 4 : 2, 1, 4, 4));
            optionsPanel.setOpaque(false);

            JLabel lblNotes = new JLabel("Catatan Konsumen:");
            lblNotes.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            lblNotes.setForeground(UITheme.COLOR_TEXT_SECONDARY);

            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");
            txtNote.setBackground(UITheme.COLOR_BG_INPUT);
            txtNote.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            txtNote.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            cmbLvl.setBackground(UITheme.COLOR_BG_CARD);
            cmbLvl.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            cmbLvl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            
            if (isFood) {
                JLabel lblLvl = new JLabel("Pilih Level Pedas:");
                lblLvl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
                lblLvl.setForeground(UITheme.COLOR_TEXT_SECONDARY);

                for (int i = 0; i <= 8; i++) {
                    if (i == 0) cmbLvl.addItem("Original (Lvl 0) - Rp 11.000");
                    else if (i <= 4) cmbLvl.addItem("Level " + i + " - Rp 11.000");
                    else cmbLvl.addItem("Level " + i + " - Rp 13.000");
                }

                optionsPanel.add(lblLvl);
                optionsPanel.add(cmbLvl);
            }

            optionsPanel.add(lblNotes);
            optionsPanel.add(txtNote);'''
new_options = '''            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
            optionsPanel.setOpaque(false);

            JLabel lblNotes = new JLabel("Catatan Konsumen:");
            lblNotes.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
            lblNotes.setForeground(UITheme.COLOR_TEXT_SECONDARY);

            ModernTextField txtNote = new ModernTextField("Contoh: Tanpa kuah, sendok...");
            txtNote.setBackground(UITheme.COLOR_BG_INPUT);
            txtNote.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            txtNote.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            txtNote.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

            ModernComboBox<String> cmbLvl = new ModernComboBox<>();
            cmbLvl.setBackground(UITheme.COLOR_BG_CARD);
            cmbLvl.setForeground(UITheme.COLOR_TEXT_PRIMARY);
            cmbLvl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            cmbLvl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            
            if (isFood) {
                JLabel lblLvl = new JLabel("Pilih Level Pedas:");
                lblLvl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
                lblLvl.setForeground(UITheme.COLOR_TEXT_SECONDARY);

                for (int i = 0; i <= 8; i++) {
                    if (i == 0) cmbLvl.addItem("Original (Lvl 0) - Rp 11.000");
                    else if (i <= 4) cmbLvl.addItem("Level " + i + " - Rp 11.000");
                    else cmbLvl.addItem("Level " + i + " - Rp 13.000");
                }

                optionsPanel.add(lblLvl);
                optionsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                optionsPanel.add(cmbLvl);
                optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            optionsPanel.add(lblNotes);
            optionsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
            optionsPanel.add(txtNote);'''
acontent = acontent.replace(old_options, new_options)

with open(app_path, 'w', encoding='utf-8') as f:
    f.write(acontent)
print('Done step 3.')
