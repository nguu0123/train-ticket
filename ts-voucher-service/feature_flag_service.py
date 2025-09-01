import os
from openfeature import api
from openfeature.contrib.provider.flagd import FlagdProvider
from openfeature.contrib.provider.flagd.config import ResolverType, CacheType


class FeatureFlagService:
    def __init__(self) -> None:
        provider = FlagdProvider(
            resolver_type=ResolverType.RPC,
            host="flagd",
            port=8013,
            cache=CacheType.DISABLED,
            max_cache_size=0,
        )
        api.set_provider(provider)
        self.client = api.get_client("voucher-service")

    def is_enabled(self, flag_name: str) -> bool:
        try:
            details = self.client.get_boolean_details(flag_name, False)
            print(
                f"[TrainTicket][Voucher][FeatureFlagService] Flag {flag_name}: value={details.value}, reason={getattr(details, 'reason', 'N/A')}"
            )

            if getattr(details, "reason", None) == "ERROR":
                print(
                    f"[TrainTicket][Voucher][FeatureFlagService] Provider error for flag {flag_name}"
                )
                return False
            return bool(details.value)
        except Exception as e:
            print(
                f"[TrainTicket][Voucher][FeatureFlagService] Error getting flag {flag_name}: {e}"
            )
            return False
