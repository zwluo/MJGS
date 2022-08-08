"use strict";

// build our menu on init
jQuery( function($) {
	var mqQueryMenuMode = window.matchMedia ? window.matchMedia("(max-width: 910px)") : {matches: false};

	var $HTML = $J('html');
	window.UseTouchFriendlyMode = function() {
		return $HTML.hasClass( 'responsive' ) && ( mqQueryMenuMode.matches || $HTML.hasClass('touch') );
	};
	window.UseSmallScreenMode = function() {
		return $HTML.hasClass( 'responsive' ) && mqQueryMenuMode.matches;
	};


	// main menu

	var $Menu = $('#responsive_page_menu');
	var $Frame = $('.responsive_page_frame');
	var $ContentCtn = $('.responsive_page_content' );
	var $ContentOverlay = $('.responsive_page_content_overlay');

	var fnResetMenuState = function() {
		$Frame.removeClass( 'mainmenu_active');
		$Frame.removeClass('localmenu_active');
		$J(document.body).removeClass( 'overflow_hidden' );

		$ContentOverlay.off( 'click.ReponsiveMenuDismiss');
	};

	$J(document).on('click.OnClickDismissMenu', '.responsive_OnClickDismissMenu', fnResetMenuState );

	// 第一次访问时，默认打开神话栏目的第一篇文章
	var bookId = localStorage.getItem('bookId');
	var strLastExpandedSubmenu = localStorage.getItem( 'responsiveMenuLastSubmenu' + bookId);

	var fnMakeExpandableMenuItem = function( $MenuItem, $Submenu )
	{
		$MenuItem.append( $J('<div/>', {'class': 'chevron' } ) );


		var $SubmenuWrapper = $J('<div/>', {'class': 'menuitem_submenu_wrapper' });
		$MenuItem.after( $SubmenuWrapper.append( $Submenu ) );
		$Submenu.wrap( $('<div/>', {'class': 'inner_borders' } ) );

		// 第一次访问时，默认打开神话栏目的第一篇文章
		if (strLastExpandedSubmenu == null) {
			strLastExpandedSubmenu = localStorage.getItem( 'responsiveMenuLastSubmenu' + bookId);
		}

		// if this was the last used submenu, start with it expanded
		if ( strLastExpandedSubmenu && strLastExpandedSubmenu == $Submenu.data('submenuid') )
		{
			$SubmenuWrapper.css( 'height', $Submenu.height() + 'px' );
			$MenuItem.addClass( 'submenu_active' );
			$SubmenuWrapper.addClass('active');
		}
		else
		{
			$SubmenuWrapper.css( 'height', 0 );
		}

		$Submenu.show();

		$MenuItem.click( function(e) {
			e.preventDefault();
			if ( $SubmenuWrapper.hasClass('active' ) )
			{
				$SubmenuWrapper.removeClass('active' ).css('height',0);
				$MenuItem.removeClass('submenu_active');
				//localStorage.setItem('responsiveMenuLastSubmenu', null);
			}
			else
			{
				// 解决从传说栏目最后一篇文章，切换到下一个故事栏目时，没有展现故事栏目第一篇文章
				if ($SubmenuWrapper.siblings( '.menuitem_submenu_wrapper.active' ).length == 0) {
					$SubmenuWrapper.css( 'height', $Submenu.height() + 'px' );
					$MenuItem.addClass( 'submenu_active' );
					$SubmenuWrapper.addClass('active');
				} else {
					$SubmenuWrapper.siblings( '.menuitem_submenu_wrapper.active' ).css('height',0 ).removeClass('active');
					$MenuItem.siblings('.menuitem').removeClass('submenu_active');
					setTimeout(function() {
						$SubmenuWrapper.css( 'height', $Submenu.height() + 'px' );
						$MenuItem.addClass( 'submenu_active' );
						$SubmenuWrapper.addClass('active');
					}, 500);
				}
				//localStorage.setItem('responsiveMenuLastSubmenu', $Submenu.data('submenuid'));
			}
		});
	};

	var fnBuildMenuEvents = function( $Menu, strMenuName, fnFirstTimeInitialization )
	{
		var strActiveClass = strMenuName + '_active';
		var fnDismissMenu = function() {
			$ContentCtn.off( 'click.ReponsiveMenuDismiss');
			$Frame.removeClass(strActiveClass);
			$J(document.body).removeClass('overflow_hidden' );

			window.setTimeout( function() {
				if ( !$Frame.hasClass('mainmenu_active') && !$Frame.hasClass('localmenu_active') )
				{
					fnResetMenuState();
				}
			}, 500 );
		};

		var bInitialized = false;
		var fnActivateMenu = function() {
			if ( !bInitialized )
			{
				fnFirstTimeInitialization && fnFirstTimeInitialization();
				bInitialized = true;
			}

			if ( $Frame.hasClass( strActiveClass ) )
			{
				fnDismissMenu();
			}
			else
			{
				$J(document.body).addClass('overflow_hidden' );
				$Menu.removeClass('secondary_active');
				$Frame.addClass( strActiveClass );
				$ContentOverlay.one( 'click.ResponsiveMenuDismiss', function() {
					fnDismissMenu();
				});
			}
		};

		return { fnActivateMenu: fnActivateMenu, fnDismissMenu: fnDismissMenu };
	};

	var fnInitMainMenu = function() {
		$('.responsive_page_menu' ).find( '.supernav').each( function() {
			var $Element = $(this);
			$Element.attr('href','');
			var strSubmenuSelector = $Element.data('tooltip-content');
			var $Submenu = $Element.parent().find(strSubmenuSelector);
			if ( $Submenu.length )
			{
				fnMakeExpandableMenuItem( $Element, $Submenu );
			}
		});

		var $NotificationItem = $Menu.find( '.notifications_item' );
		var $NotificationSubmenu = $Menu.find('.notification_submenu');
		if ( $NotificationItem.length && $NotificationSubmenu.length )
		{
			fnMakeExpandableMenuItem( $NotificationItem, $NotificationSubmenu );
		}

		// 将滚动条定位到上次浏览的位置
		var bookId = localStorage.getItem('bookId');
		var taleId = localStorage.getItem("book" + bookId);
		if (taleId != null
			&& localStorage.getItem('responsiveMenuLastSubmenu' + bookId) != "null"
			&& document.getElementById(taleId) != null) {
			document.getElementById(taleId).scrollIntoView({block: "center"});
		}
	};

	var MainMenuEvents = null;
	if ( $Menu.length )
	{
		MainMenuEvents = fnBuildMenuEvents( $Menu, 'mainmenu', fnInitMainMenu );


		$('#responsive_menu_logo' ).click( function( e ) {
			MainMenuEvents.fnActivateMenu();
		} );
	}


	// local (page-specific) menu

	var $LocalMenuContent = $('.responsive_local_menu');
	var $LocalMenu = null;
	var LocalMenuEvents = null;
	if ( $LocalMenuContent.length )
	{
		var bLocalMenuEnabed = false;
		var rgMenuContents = [];

		var fnInitLocalMenu = function() {
			if ( rgMenuContents.length )
				return;

			for ( var i = 0; i < $LocalMenuContent.length; i++ )
			{
				var $LocalMenuElement = $($LocalMenuContent[i] ).wrap( $J('<div/>', {'class': 'responsive_local_menu_placeholder' } ) );
				var $LocalMenuWrapper = $($LocalMenuContent[i]).parent();
				rgMenuContents.push( {
					wrapper: $LocalMenuWrapper,
					content: $LocalMenuElement
				});
			}
		};

		$LocalMenu = $J('#responsive_page_local_menu');
		var $Affordance = $J('.responsive_local_menu_tab');

		LocalMenuEvents = fnBuildMenuEvents( $LocalMenu, 'localmenu' );

		$Affordance.click( function( e ) {
			LocalMenuEvents.fnActivateMenu();
		});

		$(window ).on( 'Responsive_SmallScreenModeToggled.ReponsiveLocalMenu', function() {
			var bShouldUseResponsiveMenu = UseSmallScreenMode();
			if ( bLocalMenuEnabed != bShouldUseResponsiveMenu )
			{
				if ( bShouldUseResponsiveMenu )
				{
					$Affordance.addClass( 'active' );
					fnInitLocalMenu();
					$LocalMenu.find('.localmenu_content' ).append( $LocalMenuContent );
				}
				else
				{
					fnResetMenuState();
					$Affordance.removeClass('active');
					for ( var i = 0; i < rgMenuContents.length; i++ )
					{
						rgMenuContents[i].wrapper.append( rgMenuContents[i].content );
					}
				}
				bLocalMenuEnabed = bShouldUseResponsiveMenu;
			}
		} ).trigger( 'Responsive_SmallScreenModeToggled.ReponsiveLocalMenu');
	}

});
