package io.github.thecsdev.tcdcommons.util;

import io.github.thecsdev.tcdcommons.TCDCommons;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.ApiStatus.Internal;

import static io.github.thecsdev.tcdcommons.api.util.TextUtils.translatable;

/**
 * {@link TCDCommons}'s {@link Component}s.
 * @apiNote Contains all translatable {@link Component}s used by this mod.
 */
public final @Internal class TCDCT
{
	// ==================================================
	public static final String KEY_RCS = "tcdcommons.key.refresh_current_screen";
	// --------------------------------------------------
	private TCDCT() {}
	// ==================================================
	public static final MutableComponent tcdc() { return translatable("tcdcommons"); }
	// --------------------------------------------------
	public static final MutableComponent tcdc_term_ghSponsors()            { return translatable("tcdcommons.github.sponsors"); }
	public static final MutableComponent tcdc_term_ghSponsors_noSponsors() { return translatable("tcdcommons.github.sponsors.no_sponsors"); }
	public static final MutableComponent tcdc_term_specialThanks()         { return translatable("tcdcommons.special_thanks"); }
	public static final MutableComponent tcdc_term_featured()              { return translatable("tcdcommons.featured"); }
	public static final MutableComponent tcdc_term_featured_noOne()        { return translatable("tcdcommons.featured.no_one"); }
	public static final MutableComponent tcdc_term_fetchFail()             { return translatable("tcdcommons.fetch_fail"); }
	//
	public static final MutableComponent tcdc_term_clientSide() { return translatable("tcdcommons.client_side"); }
	public static final MutableComponent tcdc_term_serverSide() { return translatable("tcdcommons.server_side"); }
	// --------------------------------------------------
	public static final MutableComponent pbadge_title()        { return translatable("tcdcommons.api.badge.playerbadge"); }
	public static final MutableComponent pbadge_title_plural() { return translatable("tcdcommons.api.badge.playerbadge.plural"); }
	// --------------------------------------------------
	public static final MutableComponent gui_explorer_title()        { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title"); }
	public static final MutableComponent gui_explorer_title_save()   { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_save"); }
	public static final MutableComponent gui_explorer_title_open()   { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_open"); }
	public static final MutableComponent gui_explorer_title_selDir() { return translatable("tcdcommons.api.client.gui.screen.explorer.tfilechooserscreen.title_seldir"); }
	//
	public static final MutableComponent gui_explorer_actionBar_fileName() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.file_name"); }
	public static final MutableComponent gui_explorer_actionBar_fileType() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.file_type"); }
	public static final MutableComponent gui_explorer_actionBar_saveFile() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.save_file"); }
	public static final MutableComponent gui_explorer_actionBar_openFile() { return translatable("tcdcommons.api.client.gui.panel.explorer.actionbar.open_file"); }
	//
	public static final MutableComponent gui_explorer_fileFilter_allFiles()               { return translatable("tcdcommons.api.util.interfaces.tfilefilter.all_files"); }
	public static final MutableComponent gui_explorer_fileFilter_extFiles(Component extension) { return translatable("tcdcommons.api.util.interfaces.tfilefilter.x_files", extension); }
	//
	public static final MutableComponent gui_explorer_fileList_errNoSuchDir()       { return translatable("tcdcommons.api.client.gui.panel.explorer.filelistpanel.err_nosuchdir"); }
	public static final MutableComponent gui_explorer_fileList_errNoAccess()        { return translatable("tcdcommons.api.client.gui.panel.explorer.filelistpanel.err_noaccess"); }
	public static final MutableComponent gui_explorer_fileList_errOther(Component error) { return translatable("tcdcommons.api.client.gui.panel.explorer.filelistpanel.err_other", error); }
	// --------------------------------------------------
	public static final MutableComponent gui_panel_scrollTip() { return translatable("tcdcommons.api.client.gui.panel.tpanelelement.mouse_scroll_tip"); }
	// --------------------------------------------------
	public static final MutableComponent gui_wSelect_defLabel() { return translatable("tcdcommons.api.client.gui.widget.tselectwidget.default_label"); }
	// --------------------------------------------------
	public static final MutableComponent cmd_pb_edit_out(Component badge, Component affectedPlayerCount) { return translatable("commands.badges.edit.output", badge, affectedPlayerCount); }
	public static final MutableComponent cmd_pb_clear_out(Component affectedPlayerCount)            { return translatable("commands.badges.clear.output", affectedPlayerCount); }
	public static final MutableComponent cmd_pb_chatGrant(Component player, Component badge)             { return translatable("commands.badges.chat_grant", player, badge); }
	public static final MutableComponent cmd_pb_query_out(Component player, Component badge, Component value) { return translatable("commands.badges.query.output", player, badge, value); }
	// --------------------------------------------------
	public static final MutableComponent key_refrshCurrentScreen() { return translatable(KEY_RCS); }
	// ==================================================
}