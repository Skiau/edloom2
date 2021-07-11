package edloom.graphics;

import javax.swing.*;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class MyComboBox<Object> extends JComboBox<Object> {

    public MyComboBox(Object[] array, int width) {
        super(array);
        setUI(ColorArrowUI.createUI());
        setBackground(Color.white);
        setPreferredSize(new Dimension(width, 15));
        setMaximumRowCount(5);
        JComboBox<Object> box = this;
        setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup((JComboBox<java.lang.Object>) box) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        scroller.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                            @Override
                            protected JButton createDecreaseButton(int orientation) {
                                return createZeroButton();
                            }

                            @Override
                            protected JButton createIncreaseButton(int orientation) {
                                return createZeroButton();
                            }

                            @Override
                            public Dimension getPreferredSize(JComponent c) {
                                return new Dimension(0, super.getPreferredSize(c).height);
                            }

                            private JButton createZeroButton() {
                                return new JButton() {
                                    @Override
                                    public Dimension getMinimumSize() {
                                        return new Dimension(new Dimension(0, 0));
                                    }

                                    @Override
                                    public Dimension getPreferredSize() {
                                        return new Dimension(new Dimension(0, 0));
                                    }

                                    @Override
                                    public Dimension getMaximumSize() {
                                        return new Dimension(new Dimension(0, 0));
                                    }
                                };
                            }
                        });
                        return scroller;
                    }
                };
            }
        });

    }


    static class ColorArrowUI extends BasicComboBoxUI {

        public static ComboBoxUI createUI() {
            return new ColorArrowUI();
        }

        @Override
        protected JButton createArrowButton() {
            return new BasicArrowButton(
                    BasicArrowButton.SOUTH,
                    null,null,
                    Color.DARK_GRAY, null);
        }
    }
}
